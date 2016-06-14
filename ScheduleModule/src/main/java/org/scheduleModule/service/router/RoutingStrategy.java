package org.scheduleModule.service.router;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;

import java.util.List;
import java.util.Set;

/**
 * Created by fantasy on 2016/5/22.
 */
public interface RoutingStrategy {
    Set<Engine> getDestination(String token);
}
