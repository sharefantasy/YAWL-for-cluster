package org.yawlfoundation.cluster.scheduleModule.service.allocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;

/**
 * Created by fantasy on 2016/7/31.
 */
@Component
public class HashMod implements AllocationStrategy {

	@Autowired
	private EngineRepo engineRepo;

	@Override
	public Engine allocate(Tenant tenant, Case caseToAllocate) {
		int index = caseToAllocate.hashCode() % tenant.getEngineSet().size();
		int count = 0;
		Engine defaultEngine = null;
		for (String s : tenant.getEngineSet()) {
			if (count == 0)
				defaultEngine = engineRepo.findOne(s);
			if (count == index)
				return engineRepo.findOne(s);
			count++;
		}
		return defaultEngine;
	}
}
