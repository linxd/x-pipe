package com.ctrip.xpipe.redis.console.healthcheck.nonredis.cluster;

import com.ctrip.xpipe.api.observer.Observer;
import com.ctrip.xpipe.redis.checker.ClusterHealthManager;
import com.ctrip.xpipe.redis.checker.healthcheck.RedisHealthCheckInstance;

import java.util.Map;
import java.util.Set;

public interface ClusterHealthMonitorManager extends ClusterHealthManager {

    void updateHealthCheckWarningShards(Map<String, Set<String>> warningClusterShards);

    void healthCheckMasterDown(RedisHealthCheckInstance instance);

    void healthCheckMasterUp(RedisHealthCheckInstance instance);

    void outerClientMasterDown(String clusterId, String shardId);

    void outerClientMasterUp(String clusterId, String shardId);

    Set<String> getWarningClusters(ClusterHealthState state);

    Observer createHealthStatusObserver();
}
