package org.scheduleModule.service.translate;

import org.scheduleModule.entity.Engine;

/**
 * Created by fantasy on 2016/5/16.
 */
public interface Translator {

    String publicToInternal(String orginal, Engine engine);

    String internalToPublic(String orginal, Engine engine);

}
