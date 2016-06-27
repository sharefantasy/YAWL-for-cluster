package org.yawlfoundation.cluster.scheduleModule.service;


import org.dom4j.Attribute;
import org.dom4j.Element;
import org.yawlfoundation.cluster.scheduleModule.service.merge.MergeAction;
import org.yawlfoundation.cluster.scheduleModule.service.merge.MergeCriteria;
import org.yawlfoundation.cluster.scheduleModule.service.merge.MergeRule;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class MergeRuleFactory {
    private static final MergeAction Combine = (base, child, criteria) -> {
        Element toCombineTo = criteria.choose(base, child);
        if (toCombineTo == null) {
            base.add(child);
        } else {
            ((List<Element>) child.elements()).forEach(e -> toCombineTo.add(e.createCopy()));
        }
    };
    private static final MergeAction Append = (base, child, criteria) -> base.add(child.createCopy());
    private static final MergeAction Complement = (base, child, criteria) -> {
        Element toComplementTo = criteria.choose(base, child);
        if (toComplementTo == null) {
            base.add(child.createCopy());
        }
    };
    private static final MergeCriteria Name = (base, tomerge) -> {
        List<Element> children = (List<Element>) base.elements(tomerge.getName());
        return children.isEmpty() ? null : children.get(0);
    };
    private static final MergeCriteria Name_Attribute = (base, tomerge) -> {
        List<Element> children = base.elements(tomerge.getName());
        if (children.isEmpty())
            return null;
        for (Element child : children) {
            if (equalAttributes(child, tomerge))
                return child;
        }
        return null;
    };
    private static final MergeCriteria Name_Attribute_Content = (Element base, Element tomerge) -> {
        List<Element> children = base.elements(tomerge.getName());
        if (children.isEmpty())
            return null;
        for (Element child : children) {
            if (equalAttributes(child, tomerge) &&
                    equalsContent(child, tomerge))
                return child;
        }
        return null;
    };
    public static final MergeRule DEFAULT_MERGE_RULE = new MergeRule(Append, Name);

    public static MergeRule buildMergeRule(String action, String criterion) {
        return new MergeRule(actions.get(action), criteria.get(criterion));
    }

    protected static final HashMap<String, MergeAction> actions = new HashMap<String, MergeAction>() {{
        put("combine", Combine);
        put("append", Append);
        put("complement", Complement);
    }};
    protected static final HashMap<String, MergeCriteria> criteria = new HashMap<String, MergeCriteria>() {{
        put("name", Name);
        put("name_attribute", Name_Attribute);
        put("name_attribute_content", Name_Attribute_Content);
    }};

    protected static boolean equalAttributes(Element element1, Element element2) {
        List<Attribute> attributes1 = element1.attributes();
        List<Attribute> attributes2 = element2.attributes();
        return attributes1.size() == attributes2.size()
                && attributes1.stream().filter(attribute ->
                !element2.attributeValue(attribute.getName())
                        .equals(attribute.getValue())).count() == 0;
    }

    protected static boolean equalsContent(Element element1, Element element2) {
        return element1.toString().equals(element2.toString());
    }
}
