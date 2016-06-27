package org.yawlfoundation.cluster.scheduleModule.service.translate;

import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by fantasy on 2016/5/17.
 */
@Component
public class PublicToInternal extends AbstractValueTranslator {
    @Autowired
    private CaseRepo caseRepo;

    @Override
    public String Case(String caseId, Engine engine) {
        if (caseId == null) {
            throw new IllegalArgumentException("case name is null");
        }
        String rootCaseId = caseId.split("\\.")[0];
        Case rootCase = caseRepo.findOne(rootCaseId);
        if (rootCase == null)
            throw new IllegalStateException(String.format("can't found case internal(%s, %s)", caseId, engine.getId()));
        engine = rootCase.getEngine();
        return caseId.replaceFirst(rootCaseId, rootCase.getInternalId());
    }


}
