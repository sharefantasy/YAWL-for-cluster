package org.scheduleModule.service.allocation;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;

/**
 * Created by fantasy on 2016/6/15.
 */
@FunctionalInterface
public interface AllocationStrategy {
    //rr allocation
    Engine allocate(Tenant tenant);
}
