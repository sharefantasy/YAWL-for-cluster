package org.yawlfoundation.cluster.scheduleModule.service.translate;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by fantasy on 2016/5/9.
 */

@Service
@Transactional
public class RequestTranslator extends AbstractTranslator {
    @Autowired
    @Qualifier("internalToPublic")
    private ValueTranslator i2p;
    @Autowired
    @Qualifier("publicToInternal")
    private ValueTranslator p2i;

    public Map<String, String> publicToInternal(Map<String, String> orginalParams, Engine engine) {
        if (orginalParams == null) {
            throw new IllegalArgumentException("invalid parameters");
        }

        return walkMap(orginalParams, engine, p2i);
    }

    public Map<String, String> internalToPublic(Map<String, String> orginalParams, Engine engine) {
        if (orginalParams == null) {
            throw new IllegalArgumentException("invalid parameters");
        }

        return walkMap(orginalParams, engine, i2p);
    }

    private Map<String, String> walkMap(Map<String, String> orginParams, Engine engine, ValueTranslator translator) {
        Map<String, String> translated = new HashMap<>();
        for (Map.Entry<String, String> entry : orginParams.entrySet()) {
            String result;
            if (entry.getKey().equalsIgnoreCase("workitem")) {
                result = walkDocument(entry.getValue(), engine, translator);
            } else if ((entry.getKey().equalsIgnoreCase("caseid") || entry.getKey().equals("id"))) {
                result = translator.Case(entry.getValue(), engine);
            } else {
                result = translateValue(entry.getKey(), entry.getValue(), engine, translator);
            }
            translated.put(entry.getKey(), result);
        }
        return translated;
    }


}
