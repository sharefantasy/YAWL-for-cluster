package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by fantasy on 2016/6/6.
 */
@Component
public class AnyEngineInTenant extends RoutingRule {
    protected static final AnyEngineInTenant instance = new AnyEngineInTenant();

    public static AnyEngineInTenant getInstance() {
        return instance;
    }

    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            String result = sendWithSessionRetry(e, params, interfce);
            if (result != null) {
                return result;
            }
        }
        return SchedulerUtils.failure("cannot get connect to any engine");
    }
}
