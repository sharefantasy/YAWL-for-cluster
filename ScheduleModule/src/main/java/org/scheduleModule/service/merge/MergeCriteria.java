package org.scheduleModule.service.merge;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import java.util.List;

/**
 * Created by fantasy on 2016/6/4.
 */
@FunctionalInterface
public interface MergeCriteria {
    Element choose(Element base, Element tomerge);

}