package org.scheduleModule.service.router.strategy;

import org.scheduleModule.entity.Case;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by fantasy on 2016/6/6.
 */
@Component
public class OneEngineByCaseOrWorkitem extends RoutingRule {
    protected static final OneEngineByCaseOrWorkitem instance = new OneEngineByCaseOrWorkitem();

    public static OneEngineByCaseOrWorkitem getInstance() {
        return instance;
    }
    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        String caseId = null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("caseid")) {
                caseId = entry.getValue();
                break;
            } else if (entry.getKey().equalsIgnoreCase("workitemid")) {
                caseId = getCaseId(entry.getValue());
                break;
            }
        }
        if (caseId == null) {
            return SchedulerUtils.failure("Invalid Action");
        }
        Case c = caseRepo.findOne(caseId);
        if (c == null) {
            return SchedulerUtils.failure("no such case");
        }

        return sendWithSessionRetry(c.getEngine(), params, interfce);
    }

    protected String getCaseId(String workItemID) {
        if (workItemID.contains(":")) {
            int delim1 = workItemID.indexOf(":");
            return workItemID.substring(0, delim1);
        }
        return null;
    }
}
