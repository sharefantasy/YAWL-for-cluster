package org.scheduleModule.service.translate;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.util.SchedulerUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by fantasy on 2016/5/17.
 */
public abstract class AbstractTranslator {

    private static final Logger _logger = Logger.getLogger(AbstractTranslator.class);

    protected String walkDocument(String doc, Engine engine, ValueTranslator translator) {
        Document document;
        try {
            document = SchedulerUtils.stringToDocument(doc);
        } catch (JDOMException | IOException e) {
            return doc;
        }
        walkElement(document.getRootElement(), engine, translator);
        return "" + SchedulerUtils.documentToString(document);
    }

    protected void walkElement(Element element, Engine engine, ValueTranslator translator) {
        for (Attribute a : element.getAttributes()) {
            a.setValue(translateValue(a.getName(), a.getValue(), engine, translator));
        }
        List<Element> children = element.getChildren();
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
