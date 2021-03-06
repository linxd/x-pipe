package com.ctrip.xpipe.redis.checker.healthcheck.actions.crdtredisconf;

import com.ctrip.xpipe.redis.checker.healthcheck.BiDirectionSupport;
import com.ctrip.xpipe.redis.checker.healthcheck.RedisHealthCheckInstance;
import com.ctrip.xpipe.redis.checker.healthcheck.actions.redisconf.AbstractRedisConfigCheckRuleActionFactory;
import com.ctrip.xpipe.redis.checker.healthcheck.actions.redisconf.RedisConfigCheckRuleActionListener;
import com.ctrip.xpipe.redis.checker.healthcheck.leader.SiteLeaderAwareHealthCheckAction;
import org.springframework.stereotype.Component;



@Component
public class CRDTRedisConfigCheckRuleActionFactory extends AbstractRedisConfigCheckRuleActionFactory implements BiDirectionSupport {
    private static final String CRDT_CONFIG_CHECK_TYPE = "crdt.config";

    @Override
    public SiteLeaderAwareHealthCheckAction create(RedisHealthCheckInstance instance) {
        CRDTRedisConfigCheckRuleAction crdtRedisConfigCheckRuleAction =
                new CRDTRedisConfigCheckRuleAction(scheduled, instance, executors, filterNonConifgRule(instance.getCheckInfo().getRedisCheckRules(), CRDT_CONFIG_CHECK_TYPE));
        crdtRedisConfigCheckRuleAction.addListener(new RedisConfigCheckRuleActionListener(alertManager));
        crdtRedisConfigCheckRuleAction.addControllers(controllersByClusterType.get(instance.getCheckInfo().getClusterType()));
        return crdtRedisConfigCheckRuleAction;
    }

    @Override
    public Class<? extends SiteLeaderAwareHealthCheckAction> support() {
        return CRDTRedisConfigCheckRuleAction.class;
    }


    @Override
    public String getCheckType() {
        return CRDT_CONFIG_CHECK_TYPE;
    }
}
