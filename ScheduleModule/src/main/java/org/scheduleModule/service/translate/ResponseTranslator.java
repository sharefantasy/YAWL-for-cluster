package org.scheduleModule.service.translate;

import org.scheduleModule.entity.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fantasy on 2016/5/16.
 */
@Service
@Transactional
public class ResponseTranslator extends AbstractTranslator implements Translator {


    @Autowired
    @Qualifier("internalToPublic")
    private ValueTranslator i2p;
    @Autowired
    @Qualifier("publicToInternal")
    private ValueTranslator p2i;


    @Override
    public String publicToInternal(String orginal, Engine engine) {
        if (orginal == null) {
            return null;
        }
        return walkDocument(orginal, engine, p2i);
    }

    @Override
    public String internalToPublic(String orginal, Engine engine) {
        if (orginal == null) {
            return null;
        }
        return walkDocument(orginal, engine, i2p);
    }


}
