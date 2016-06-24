package org.scheduleModule.service.merge;

import org.jdom2.Element;

/**
 * Created by fantasy on 2016/6/4.
 */

@FunctionalInterface
public interface MergeAction {
    void act(Element base, Element child, MergeCriteria criteria);
}