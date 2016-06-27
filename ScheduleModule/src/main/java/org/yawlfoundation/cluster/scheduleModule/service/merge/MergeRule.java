package org.yawlfoundation.cluster.scheduleModule.service.merge;

/**
 * Created by fantasy on 2016/6/6.
 */
public class MergeRule {
    public MergeAction mergeAction;
    public MergeCriteria mergeCriteria;

    public MergeRule(MergeAction mergeAction, MergeCriteria mergeCriteria) {
        this.mergeAction = mergeAction;
        this.mergeCriteria = mergeCriteria;
    }

}
