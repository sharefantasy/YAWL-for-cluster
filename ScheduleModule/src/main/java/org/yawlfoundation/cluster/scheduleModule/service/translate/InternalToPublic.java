package org.yawlfoundation.cluster.scheduleModule.service.translate;

import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fantasy on 2016/5/17.
 */
@Component
public class InternalToPublic extends AbstractValueTranslator {

    @Autowired
    private CaseRepo caseRepo;

    @Override
    public String Case(String caseId, Engine engine) {
        if (caseId == null) {
            throw new IllegalArgumentException("case name is null");
        }
        String rootCaseId = caseId.split("\\.")[0];
        List<Case> rootCase = caseRepo.findByInternalId(rootCaseId);

        Case theCase = rootCase.stream().filter(c -> c.getEngine().getId().equals(engine.getId())).findFirst().get();
        if (theCase == null)
            throw new IllegalStateException(String.format("can't found case internal(%s, %s)", caseId, engine.getId()));
        return caseId.replaceFirst(rootCaseId, theCase.getId());
    }
}
