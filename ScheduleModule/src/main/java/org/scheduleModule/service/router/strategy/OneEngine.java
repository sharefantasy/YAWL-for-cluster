package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.router.RoutingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fantasy on 2016/6/6.
 */
@Component
public class OneEngine implements RoutingStrategy {
    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private EngineRepo engineRepo;

    @Override
    public Set<Engine> getDestination(String token) {
        Set<Engine> result = new HashSet<>();
        Set<String> engines = tenantRepo.findOne(token).getEngineSet();
        for (String e : engines) {
            result.add(engineRepo.findOne(e));
            break;
        }
        return result;
    }
}
