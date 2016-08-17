package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.yawlfoundation.cluster.scheduleModule.service.allocation.AllocationStrategy;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;

/**
 * Created by fantasy on 2016/6/15.
 */
@Component
public class NewAllocationByTenant extends RoutingRule {
    protected static final NewAllocationByTenant instance = new NewAllocationByTenant();

    public static NewAllocationByTenant getInstance() {
        return instance;
    }

	@Qualifier("hashMod")
    @Autowired
    private AllocationStrategy strategy;
    @Autowired
    private CaseRepo caseRepo;

    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
		Engine engine = strategy.allocate(tenant, null);
        params = requestTranslator.publicToInternal(params, engine);
        String result = sendUntranslated(engine, params, interfce);
		String internalID = SchedulerUtils.unwrap(result);
		Case c = new Case(internalID, engine);
        caseRepo.save(c);
        return c.getId();
    }
}
