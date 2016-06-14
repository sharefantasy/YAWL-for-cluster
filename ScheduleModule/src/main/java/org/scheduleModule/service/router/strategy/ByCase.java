package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Case;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.service.router.RoutingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fantasy on 2016/6/6.
 */
@Component
public class ByCase implements RoutingStrategy {
    @Autowired
    private CaseRepo caseRepo;

    @Override
    public Set<Engine> getDestination(String token) {
        Case c = caseRepo.findOne(token);
        if (c != null) {
            Set<Engine> result = new HashSet<>();
            result.add(c.getEngine());
            return result;
        }
        return null;
    }
}
