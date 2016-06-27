package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.service.MergeService;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by fantasy on 2016/5/22.
 */
@Component
public class AllEngineInTenant extends RoutingRule {
    @Autowired
    protected MergeService mergeService;

    protected static final AllEngineInTenant instance = new AllEngineInTenant();

    public static AllEngineInTenant getInstance() {
        return instance;
    }

    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        List<String> results = new ArrayList<>();
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            String result = sendWithSessionRetry(e, params, interfce);
            results.add(result);
        }
        return SchedulerUtils.wrap(mergeService.merge(results, params.get("action")));
    }
}
