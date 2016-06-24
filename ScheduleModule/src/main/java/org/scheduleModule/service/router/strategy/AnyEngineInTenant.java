package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Response;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.util.SchedulerUtils;
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
            params.replace("sessionHandle", connectionService.getSession(e));
            String result = sendWithSessionRetry(e, params, interfce);
            if (result != null) {
                return result;
            }
        }
        return SchedulerUtils.failure("cannot get connect to any engine");
    }
}
