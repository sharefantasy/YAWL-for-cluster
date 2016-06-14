package org.scheduleModule.service.merge;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import java.util.List;

/**
 * Created by fantasy on 2016/6/4.
 */
public abstract class MergeCriteria {
    abstract public Element choose(Element base, Element tomerge);

    protected Boolean equalAttributes(Element element1, Element element2) {
        List<Attribute> attributes1 = element1.getAttributes();
        List<Attribute> attributes2 = element2.getAttributes();

        if (attributes1.size() != attributes2.size())
            return false;

        for (Attribute attribute : attributes1) {
            if (!element2.getAttributeValue(attribute.getName()).equals(attribute.getValue()))
                return false;
        }
        return true;
    }

    protected Boolean equalContent(Element element1, Element element2) {
        XMLOutputter out = new XMLOutputter();
        return out.outputString(element1).equals(out.outputString(element2));
    }
}