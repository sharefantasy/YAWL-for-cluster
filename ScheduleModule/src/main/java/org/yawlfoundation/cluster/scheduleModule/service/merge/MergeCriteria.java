package org.yawlfoundation.cluster.scheduleModule.service.merge;


import org.dom4j.Element;

/**
 * Created by fantasy on 2016/6/4.
 */
@FunctionalInterface
public interface MergeCriteria {
    Element choose(Element base, Element tomerge);

}