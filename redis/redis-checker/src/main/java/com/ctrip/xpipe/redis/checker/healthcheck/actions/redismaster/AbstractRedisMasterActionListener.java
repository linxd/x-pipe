package com.ctrip.xpipe.redis.checker.healthcheck.actions.redismaster;

import com.ctrip.xpipe.api.command.CommandFuture;
import com.ctrip.xpipe.api.command.CommandFutureListener;
import com.ctrip.xpipe.api.server.Server;
import com.ctrip.xpipe.command.AbstractCommand;
import com.ctrip.xpipe.endpoint.HostPort;
import com.ctrip.xpipe.redis.checker.PersistenceCache;
import com.ctrip.xpipe.redis.checker.healthcheck.RedisHealthCheckInstance;
import com.ctrip.xpipe.redis.checker.healthcheck.RedisInstanceInfo;
import com.ctrip.xpipe.redis.core.entity.*;
import com.ctrip.xpipe.redis.core.meta.MetaCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AbstractRedisMasterActionListener implements RedisMasterActionListener {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRedisMasterActionListener.class);

    private PersistenceCache persistenceCache;
    private ExecutorService executors;
    protected MetaCache metaCache;


    public AbstractRedisMasterActionListener(PersistenceCache persistenceCache, MetaCache metaCache, ExecutorService executors) {
        this.persistenceCache = persistenceCache;
        this.metaCache = metaCache;
        this.executors = executors;
    }

    @Override
    public void onAction(RedisMasterActionContext redisMasterActionContext) {
        RedisHealthCheckInstance instance = redisMasterActionContext.instance();

        if(!redisMasterActionContext.isSuccess()) {
            handleUnknownRole(instance);
            return;
        }

        Server.SERVER_ROLE redisRole = redisMasterActionContext.getResult().getServerRole();
        boolean actualMaster = redisRole.equals(Server.SERVER_ROLE.MASTER);
        RedisRoleState state = RedisRoleState.getFrom(instance.getCheckInfo().isMaster(), actualMaster);
        if(state.shouldBeCorrect()) {
            logger.info("[onAction][{}] {}", instance.getCheckInfo(), state);
            persistenceCache.updateRedisRole(instance, redisRole);
        }
    }

    protected void handleUnknownRole(RedisHealthCheckInstance instance) {
        RedisInstanceInfo info = instance.getCheckInfo();
        String dcId = info.getDcId();
        String clusterId = info.getClusterId();
        String shardId = info.getShardId();

        if (!info.isMaster()) {
            logger.info("[handleUnknownRole][{}] redis role unknown, skip", info.getHostPort());
            return;
        }

        List<HostPort> masters = findMasterInDcClusterShard(dcId, clusterId, shardId);
        if (!masters.contains(info.getHostPort())) {
            logger.info("[handleUnknownRole][{}-{}-{}] expected master {} is not equal to masters in meta cache {}, skip",
                    dcId, clusterId, shardId, info.getHostPort(), masters);
            return;
        }
        if (masters.size() <= 1) {
            logger.info("[handleUnknownRole][{}-{}-{}] no confuse on master {}, skip", dcId, clusterId, shardId, info.getHostPort());
            return;
        }

        new FinalMasterJudgeCommand(dcId, clusterId, shardId).execute(executors).addListener(new CommandFutureListener<HostPort>() {
            @Override
            public void operationComplete(CommandFuture<HostPort> commandFuture) throws Exception {
                if (commandFuture.isSuccess()) {
                    HostPort finalMaster = commandFuture.get();

                    if (finalMaster.equals(info.getHostPort())) {
                        logger.info("[handleUnknownRole] {} consider master this master {}", getServerName(), finalMaster);
                    } else {
                        logger.info("[handleUnknownRole] {} consider {} as master, not {}", getServerName(), finalMaster, info.getHostPort());
                        persistenceCache.updateRedisRole(instance, Server.SERVER_ROLE.SLAVE);
                    }
                } else {
                    logger.info("[FinalMasterJudgeCommand][{}-{}-{}] get master from {} fail", dcId, clusterId, shardId, getServerName(), commandFuture.cause());
                }
            }
        });
    }

    protected List<HostPort> findMasterInDcClusterShard(String dcId, String clusterId, String shardId) {
        List<HostPort> masters = new ArrayList<>();

        XpipeMeta xpipeMeta = metaCache.getXpipeMeta();
        if (null == xpipeMeta) return masters;

        DcMeta dcMeta = xpipeMeta.findDc(dcId);
        if (null == dcMeta) return masters;

        ClusterMeta clusterMeta = dcMeta.findCluster(clusterId);
        if (null == clusterMeta) return masters;

        ShardMeta shardMeta = clusterMeta.findShard(shardId);
        if (null == shardMeta) return masters;

        shardMeta.getRedises().forEach(redisMeta -> {
            if (redisMeta.isMaster()) masters.add(new HostPort(redisMeta.getIp(), redisMeta.getPort()));
        });

        return masters;
    }

    protected enum RedisRoleState {

        ROLE_MATCHED {
            @Override
            boolean shouldBeCorrect() {
                return false;
            }
        }, EXPECT_MASTER_ACTUAL_SLAVE {
            @Override
            boolean shouldBeCorrect() {
                return true;
            }
        }, EXPECT_SLAVE_ACTUAL_MASTER {
            @Override
            boolean shouldBeCorrect() {
                return true;
            }
        };

        abstract boolean shouldBeCorrect();

        public static RedisRoleState getFrom(boolean expectedMaster, boolean actualMaster) {
            if(expectedMaster == actualMaster) {
                return ROLE_MATCHED;
            } else if(expectedMaster) {
                return EXPECT_MASTER_ACTUAL_SLAVE;
            } else {
                return EXPECT_SLAVE_ACTUAL_MASTER;
            }
        }
    }

    class FinalMasterJudgeCommand extends AbstractCommand<HostPort> {

        private String dcId;
        private String clusterId;
        private String shardId;

        public FinalMasterJudgeCommand(String dcId, String clusterId, String shardId) {
            this.dcId = dcId;
            this.clusterId = clusterId;
            this.shardId = shardId;
        }

        @Override
        protected void doExecute() throws Exception {
            RedisMeta master = finalMaster(dcId, clusterId, shardId);
            future().setSuccess(new HostPort(master.getIp(), master.getPort()));
        }

        @Override
        protected void doReset() {

        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    protected abstract RedisMeta finalMaster(String dcId, String clusterId, String shardId);

    protected abstract String getServerName();
}
