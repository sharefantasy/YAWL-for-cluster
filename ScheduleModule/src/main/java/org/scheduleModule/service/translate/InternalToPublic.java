package org.scheduleModule.service.translate;

import org.scheduleModule.entity.Case;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.repo.CaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
        Case theCase = null;
        for (Case c : rootCase) {
            if (c.getEngine().equals(engine)) {
                theCase = c;
                break;
            }
        }
        if (theCase == null)
            throw new IllegalStateException(String.format("can't found case internal(%s, %s)", caseId, engine.getId()));
        return caseId.replaceFirst(rootCaseId, theCase.getId());
    }
}
