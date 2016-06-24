package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Response;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.service.MergeService;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
            params = requestTranslator.publicToInternal(params, e);
            String session = connectionService.getSession(e);
            params.replace("sessionHandle", session);
            String result = sendWithSessionRetry(e, params, interfce);
            results.add(result);
        }
        return mergeService.merge(results, params.get("action"));
    }
}
