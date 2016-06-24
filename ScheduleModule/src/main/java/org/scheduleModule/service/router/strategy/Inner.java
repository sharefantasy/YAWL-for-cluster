package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Tenant;
import org.scheduleModule.entity.User;
import org.scheduleModule.repo.UserRepo;
import org.scheduleModule.service.SessionService;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by fantasy on 2016/6/20.
 */
@Component
public class Inner extends RoutingRule {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserRepo userRepo;


    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        String sessionHandle = params.get("sessionHandle");
        switch (params.get("action")) {
            case "connect":
                User u = userRepo.findByUserName(params.get("userid"));
                if (u != null) {
                    if (u.getPassword().equals(params.get("password"))) {
                        return sessionService.connect(u);
                    }
                }
                return SchedulerUtils.failure("no such user.id or password is wrong");
            case "checkConnection":
                return (sessionService.checkConnection(sessionHandle))
                        ? SchedulerUtils.SUCCESS
                        : SchedulerUtils.failure("Invalid or expired session.");
            case "disconnect":
                if (!sessionService.checkConnection(sessionHandle))
                    return SchedulerUtils.failure("Invalid or expired session.");
                return sessionService.disconnect(sessionHandle)
                        ? SchedulerUtils.SUCCESS
                        : SchedulerUtils.failure("Invalid or expired session.");
            case "default":
                return SchedulerUtils.failure("Invalid Action");
        }
        return null;
    }
}
