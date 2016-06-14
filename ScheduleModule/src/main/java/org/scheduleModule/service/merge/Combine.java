package org.scheduleModule.service.merge;

import org.jdom2.Element;

public class Combine extends MergeAction {
    private static final Combine instance = new Combine();

    public static Combine getInstance() {
        return instance;
    }

    @Override
    public void act(Element base, Element child, MergeCriteria criteria) {
        Element toCombineTo = criteria.choose(base, child);
        if (toCombineTo == null) {
            base.addContent(child);
        } else {
            toCombineTo.addContent(child.removeContent());
        }
    }
}
