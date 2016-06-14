package org.scheduleModule.service.merge;

import org.jdom2.Element;

import java.util.List;

/**
 * Created by fantasy on 2016/6/4.
 */
public class NodeNameAndAttribute extends MergeCriteria {
    private static final NodeNameAndAttribute instance = new NodeNameAndAttribute();

    public static NodeNameAndAttribute getInstance() {
        return instance;
    }

    @Override
    public Element choose(Element base, Element tomerge) {
        List<Element> children = base.getChildren(tomerge.getName());
        if (children.isEmpty())
            return null;
        for (Element child : children) {
            if (equalAttributes(child, tomerge))
                return child;
        }
        return null;
    }
}
