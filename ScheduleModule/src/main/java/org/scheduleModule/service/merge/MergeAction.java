package org.scheduleModule.service.merge;

import org.jdom2.Element;

/**
 * Created by fantasy on 2016/6/4.
 */
public abstract class MergeAction {
    private static final MergeAction instance = Append.getInstance();

    public static MergeAction getInstance() {
        return instance;
    }

    public abstract void act(Element base, Element child, MergeCriteria criteria);
}