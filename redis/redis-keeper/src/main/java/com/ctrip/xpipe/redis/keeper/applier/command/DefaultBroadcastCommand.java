package com.ctrip.xpipe.redis.keeper.applier.command;

import com.ctrip.xpipe.client.redis.AsyncRedisClient;
import com.ctrip.xpipe.command.AbstractCommand;
import com.ctrip.xpipe.redis.core.redis.operation.RedisOp;

/**
 * @author Slight
 * <p>
 * Jun 01, 2022 09:48
 */
public class DefaultBroadcastCommand extends AbstractCommand<Boolean> implements RedisOpCommand<Boolean> {

    final AsyncRedisClient client;

    final RedisOp redisOp;

    public DefaultBroadcastCommand(AsyncRedisClient client, RedisOp redisOp) {
        this.client = client;
        this.redisOp = redisOp;
    }

    @Override
    protected void doExecute() throws Throwable {

        /* TODO: we should get masters only */
        Object[] resources = client.broadcast();
        Object[] rawArgs = redisOp.buildRawOpArgs();

        for (Object rc : resources) {

            client
                    .write(rc, rawArgs)
                    .addListener(f->{
                        /* TODO: future() might be already completed */
                        if (f.isSuccess()) {
                            future().setSuccess(true);
                        } else {
                            future().setFailure(f.cause());
                        }
                    });
        }
    }

    @Override
    protected void doReset() {

    }

    @Override
    public RedisOp redisOp() {
        return redisOp;
    }
}
