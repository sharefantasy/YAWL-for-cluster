package org.yawlfoundation.cluster.scheduleModule.service.allocation;

import org.springframework.stereotype.Component;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;

/**
 * Created by fantasy on 2016/7/9.
 */

public class AllocateByMutexRule implements AllocationStrategy {
	@Override
	public Engine allocate(Tenant tenant) {
		return null;
	}
}