package com.ctrip.xpipe.redis.core.redis.operation.parser;

import com.ctrip.xpipe.redis.core.redis.operation.*;
import com.ctrip.xpipe.redis.core.redis.operation.op.RedisOpSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lishanglin
 * date 2022/2/18
 */
@Component
public class RedisOpSetParser implements RedisOpParser {

    @Autowired
    public RedisOpSetParser(RedisOpParserManager redisOpParserManager) {
        redisOpParserManager.registerParser(RedisOpType.SET, this);
    }

    @Override
    public RedisOp parse(List<String> args) {
        return new RedisOpSet(args, new RedisKey(args.get(1)), args.get(2));
    }

    @Override
    public int getOrder() {
        return 0;
    }

}