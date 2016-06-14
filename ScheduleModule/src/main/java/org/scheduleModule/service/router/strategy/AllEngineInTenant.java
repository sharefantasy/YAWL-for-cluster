package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.router.RoutingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by fantasy on 2016/5/22.
 */
@Component
public class AllEngineInTenant implements RoutingStrategy {

    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private CaseRepo caseRepo;
    @Autowired
    private EngineRepo engineRepo;

    @Override
    public Set<Engine> getDestination(String token) {
        Set<Engine> engines = new HashSet<>();
        for (String s : tenantRepo.findOne(token).getEngineSet()) {
            engines.add(engineRepo.findOne(s));
        }
        return engines;
    }
}
