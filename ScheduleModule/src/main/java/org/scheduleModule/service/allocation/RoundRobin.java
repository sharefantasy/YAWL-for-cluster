package org.scheduleModule.service.allocation;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.EngineRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2016/6/7.
 */
@Component
public class RoundRobin implements AllocationStrategy {
    Map<Tenant, Iterator<String>> engines = new ConcurrentHashMap<>();
    @Autowired
    private EngineRepo engineRepo;

    //rr allocation
    @Override
    public Engine allocate(Tenant tenant) {
        Iterator<String> iter = engines.get(tenant);
        if (iter == null) {
            iter = tenant.getEngineSet().iterator();
        }
        if (!iter.hasNext()) {
            iter = tenant.getEngineSet().iterator();
            engines.replace(tenant, iter);
        }
        return engineRepo.findOne(iter.next());
    }
}
