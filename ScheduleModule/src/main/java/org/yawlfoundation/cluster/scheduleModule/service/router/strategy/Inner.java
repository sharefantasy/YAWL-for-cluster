package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.yawlfoundation.cluster.scheduleModule.repo.UserRepo;
import org.yawlfoundation.cluster.scheduleModule.service.SessionService;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        String result = null;
        switch (params.get("action")) {
            case "connect":
                User u = userRepo.findByUserName(params.get("userid"));
                if (u != null) {
                    if (u.getPassword().equals(params.get("password"))) {
                        result = sessionService.connect(u);
                    }
                } else {
                    result = SchedulerUtils.failure("no such user.id or password is wrong");
                }
                break;
            case "checkConnection":
                result = (sessionService.checkConnection(sessionHandle))
                        ? SchedulerUtils.SUCCESS
                        : SchedulerUtils.failure("Invalid or expired session.");
                break;
            case "disconnect":
                if (!sessionService.checkConnection(sessionHandle))
                    result = SchedulerUtils.failure("Invalid or expired session.");
                else {
                    result = sessionService.disconnect(sessionHandle)
                            ? SchedulerUtils.SUCCESS
                            : SchedulerUtils.failure("Invalid or expired session.");
                }
                break;
            case "default":
                _logger.error(params.get("action"));
                result = SchedulerUtils.failure("Invalid Action");
                break;

        }
        return result == null ? null : String.format("<response>%s</response>", result);
    }
}
