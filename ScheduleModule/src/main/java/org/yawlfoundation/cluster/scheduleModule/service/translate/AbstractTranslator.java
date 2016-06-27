package org.yawlfoundation.cluster.scheduleModule.service.translate;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;

import java.util.List;

/**
 * Created by fantasy on 2016/5/17.
 */
public abstract class AbstractTranslator {

    private static final Logger _logger = Logger.getLogger(AbstractTranslator.class);

    protected String walkDocument(String doc, Engine engine, ValueTranslator translator) {
        Document document;
        try {
            document = DocumentHelper.parseText(doc);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        walkElement(document.getRootElement(), engine, translator);
        return SchedulerUtils.documentToString(document);
    }

    protected void walkElement(Element element, Engine engine, ValueTranslator translator) {
        for (Attribute a : (List<Attribute>) element.attributes()) {
            a.setValue(translateValue(a.getName(), a.getValue(), engine, translator));
        }
        List<Element> children = element.elements();
        if (children.size() > 0) {
            for (Element e : children) {
                walkElement(e, engine, translator);
            }
        } else {
            element.setText(translateValue(element.getName(), element.getText(), engine, translator));
        }
    }

    protected String translateValue(String name, String value, Engine engine, ValueTranslator translator) {
        if (name.equalsIgnoreCase("uniqueid") || name.equalsIgnoreCase("workitemid")) {
            return translator.Workitem(value, engine);
        } else if (name.equalsIgnoreCase("caseid")) {
            return translator.Case(value, engine);
        } else if (name.equalsIgnoreCase("specidentifier")) {
            return translator.Specidentifier(value, engine);
        }
        return value;
    }

}
