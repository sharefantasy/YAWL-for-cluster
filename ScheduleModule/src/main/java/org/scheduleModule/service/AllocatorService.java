package org.scheduleModule.service;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2016/6/7.
 */
@Service
public class AllocatorService {
    Map<Tenant, Iterator<String>> engines = new ConcurrentHashMap<>();
    @Autowired
    private EngineRepo engineRepo;
    @Autowired
    private TenantRepo tenantRepo;

    //rr allocation
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
