package org.yawlfoundation.cluster.scheduleModule.service.allocation;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;

/**
 * Created by fantasy on 2016/6/15.
 */
@FunctionalInterface
public interface AllocationStrategy {
    //rr allocation
    Engine allocate(Tenant tenant);
}
