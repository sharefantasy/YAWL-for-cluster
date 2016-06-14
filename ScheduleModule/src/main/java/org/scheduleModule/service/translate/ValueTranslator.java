package org.scheduleModule.service.translate;

import org.scheduleModule.entity.Engine;

/**
 * Created by fantasy on 2016/5/17.
 */
public interface ValueTranslator {
    String Case(String caseId, Engine engine);

    String Workitem(String workitemId, Engine engine);

    String Specidentifier(String specIdentifierId, Engine engine);
}
