package org.scheduleModule.service.translate;

import org.scheduleModule.entity.Engine;

/**
 * Created by fantasy on 2016/5/17.
 */
public abstract class AbstractValueTranslator implements ValueTranslator {
    @Override
    public String Workitem(String workitemId, Engine engine) {
        if (workitemId.contains(":")) {
            // contains case number before :
            // route by case number (sub caseid's are handled by router)
            int delim1 = workitemId.indexOf(":");
            String caseID = workitemId.substring(0, delim1);
            if (caseID.contains(".")) {
                int delim2 = caseID.indexOf(".");
                caseID = caseID.substring(0, delim2);
            }

            return workitemId.replaceFirst(caseID, Case(caseID, engine));
        }

        return workitemId;
    }

    @Override
    public String Specidentifier(String specIdentifierId, Engine engine) {
        if (specIdentifierId == null) {
            throw new IllegalArgumentException("invalid specidentifier");
        }
        return specIdentifierId;
    }
}
