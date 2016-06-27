package org.yawlfoundation.cluster.scheduleModule.service.merge;

import java.util.HashMap;

public class ActSpec {
    public HashMap<String, MergeRule> mergeRule;
    public String routingRule;
    public String dest;

    public ActSpec(String name, MergeAction mergeAction, MergeCriteria mergeCriteria) {
        mergeRule = new HashMap<>();
        MergeRule rule = new MergeRule(mergeAction, mergeCriteria);
        mergeRule.put(name, rule);
    }

    public ActSpec() {
        mergeRule = new HashMap<>();
    }
}
