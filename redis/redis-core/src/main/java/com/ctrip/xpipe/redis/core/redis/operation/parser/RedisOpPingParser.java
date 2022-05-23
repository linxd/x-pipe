package com.ctrip.xpipe.redis.core.redis.operation.parser;

import com.ctrip.xpipe.redis.core.redis.operation.*;
import com.ctrip.xpipe.redis.core.redis.operation.op.RedisOpPing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lishanglin
 * date 2022/2/18
 */
@Component
public class RedisOpPingParser implements RedisOpParser {

    @Autowired
    public RedisOpPingParser(RedisOpParserManager redisOpParserManager) {
        redisOpParserManager.registerParser(RedisOpType.PING, this);
    }

    @Override
    public RedisOp parse(List<String> args) {
        return new RedisOpPing(args);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}