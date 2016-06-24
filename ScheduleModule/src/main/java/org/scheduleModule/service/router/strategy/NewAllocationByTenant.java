package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Case;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.service.allocation.AllocationStrategy;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fantasy on 2016/6/15.
 */
@Component
public class NewAllocationByTenant extends RoutingRule {
    protected static final NewAllocationByTenant instance = new NewAllocationByTenant();

    public static NewAllocationByTenant getInstance() {
        return instance;
    }

    @Autowired
    private AllocationStrategy strategy;
    @Autowired
    private CaseRepo caseRepo;

    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        Engine engine = strategy.allocate(tenant);
        String session = connectionService.getSession(engine);
        params.replace("sessionHandle", session);
        String result;
        try {
            params = requestTranslator.publicToInternal(params, engine);
            result = connectionService.forward(engine, params, interfce);
            if (SchedulerUtils.isInvalidSession(result)) {
                params.replace("sessionHandle", connectionService.getSessionOnline(engine));
                result = connectionService.forward(String.format("http://%s:%s/yawl/%s",
                        engine.getAddress(), engine.getPort(), interfce), params);
            }
        } catch (IOException e1) {
            _logger.error("cannot connect engine(+" + engine.getId() + ")");
            return SchedulerUtils.failure("fail to connect");
        }
        Case c = new Case(result, engine);
        c.setTenant(tenant);
        caseRepo.save(c);
        return c.getId();
    }
}
