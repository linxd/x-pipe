package com.ctrip.xpipe.redis.keeper.handler.applier;

import com.ctrip.xpipe.api.endpoint.Endpoint;
import com.ctrip.xpipe.endpoint.DefaultEndPoint;
import com.ctrip.xpipe.gtid.GtidSet;
import com.ctrip.xpipe.redis.core.protocal.RedisProtocol;
import com.ctrip.xpipe.redis.core.protocal.protocal.SimpleStringParser;
import com.ctrip.xpipe.redis.keeper.RedisClient;
import com.ctrip.xpipe.redis.keeper.RedisServer;
import com.ctrip.xpipe.redis.keeper.applier.ApplierServer;
import com.ctrip.xpipe.redis.keeper.handler.AbstractCommandHandler;
import com.ctrip.xpipe.utils.StringUtil;

/**
 * @author lishanglin
 * date 2022/6/11
 */
public class ApplierCommandHandler extends AbstractCommandHandler {

    public final static String GET_STATE = "getstate";

    public final static String SET_STATE = "setstate";

    public final static String STATE_ACTIVE = "active";

    public final static String STATE_BACKUP = "backup";

    @Override
    public String[] getCommands() {
        return new String[]{"applier"};
    }

    @Override
    protected void doHandle(String[] args, RedisClient<?> redisClient) throws Exception {

        if(args.length >= 1) {

            if(args[0].equalsIgnoreCase(GET_STATE)){
                // TODO
            }else if(args[0].equalsIgnoreCase(SET_STATE)){
                if(args.length >= 2 && args[1].equalsIgnoreCase(STATE_BACKUP)) {
                    ((ApplierServer)redisClient.getRedisServer()).setStateBackup();
                    redisClient.sendMessage(new SimpleStringParser(RedisProtocol.OK).format());
                } else if (args.length >= 5 && args[1].equalsIgnoreCase(STATE_ACTIVE)) {
                    Endpoint upstreamEndpoint = new DefaultEndPoint(args[2], Integer.parseInt(args[3]));
                    GtidSet gtidSet = new GtidSet(args[4]);
                    ((ApplierServer)redisClient.getRedisServer()).setStateActive(upstreamEndpoint, gtidSet);
                    redisClient.sendMessage(new SimpleStringParser(RedisProtocol.OK).format());
                } else{
                    throw new IllegalArgumentException("setstate argument error:" + StringUtil.join(" ", args));
                }
            }else{
                throw new IllegalStateException("unknown command:" + args[0]);
            }
        }
    }

    @Override
    public boolean support(RedisServer server) {
        return server instanceof ApplierServer;
    }

}