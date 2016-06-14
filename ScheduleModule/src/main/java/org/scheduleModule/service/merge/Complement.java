package org.scheduleModule.service.merge;

import org.jdom2.Element;

public class Complement extends MergeAction {
    private static final Complement instance = new Complement();

    public static Complement getInstance() {
        return instance;
    }

    @Override
    public void act(Element base, Element child, MergeCriteria criteria) {
        Element toComplementTo = criteria.choose(base, child);
        if (toComplementTo == null) {
            base.addContent(child);
        }
    }
}
