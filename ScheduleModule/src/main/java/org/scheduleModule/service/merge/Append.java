package org.scheduleModule.service.merge;

import org.jdom2.Element;

public class Append extends MergeAction {
    private static final Append instance = new Append();

    public static Append getInstance() {
        return instance;
    }

    @Override
    public void act(Element base, Element child, MergeCriteria criteria) {
        base.addContent(child);
    }
}
