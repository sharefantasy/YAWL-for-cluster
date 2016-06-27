package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
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
            String caseid = workItemID.substring(0, delim1);
            if (caseid.contains(".")) {
                int delim2 = caseid.indexOf(".");
                return caseid.substring(0, delim2);
            }
            return caseid;
        }
        return null;
    }
}
