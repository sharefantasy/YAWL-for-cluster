package org.scheduleModule.service.merge;

import org.jdom2.Element;

import java.util.List;

/**
 * Created by fantasy on 2016/6/2.
 */

public class NodeName extends MergeCriteria {
    private static final NodeName instance = new NodeName();

    public static NodeName getInstance() {
        return instance;
    }

    @Override
    public Element choose(Element base, Element tomerge) {
        List<Element> children = base.getChildren(tomerge.getName());
        return children.isEmpty() ? null : children.get(0);
    }
}
