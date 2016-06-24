package org.scheduleModule.service;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.scheduleModule.service.merge.MergeAction;
import org.scheduleModule.service.merge.MergeCriteria;
import org.scheduleModule.service.merge.MergeRule;
import org.scheduleModule.service.router.RoutingRule;
import org.scheduleModule.service.router.strategy.AllEngineInTenant;
import org.scheduleModule.service.router.strategy.AnyEngineInTenant;
import org.scheduleModule.service.router.strategy.NewAllocationByTenant;
import org.scheduleModule.service.router.strategy.OneEngineByCaseOrWorkitem;

import java.util.HashMap;
import java.util.List;

public class MergeRuleFactory {
    private static final MergeAction Combine = (base, child, criteria) -> {
        Element toCombineTo = criteria.choose(base, child);
        if (toCombineTo == null) {
            base.addContent(child);
        } else {
            toCombineTo.addContent(child.removeContent());
        }
    };
    private static final MergeAction Append = (base, child, criteria) -> base.addContent(child);
    private static final MergeAction Complement = (base, child, criteria) -> {
        Element toComplementTo = criteria.choose(base, child);
        if (toComplementTo == null) {
            base.addContent(child);
        }
    };
    private static final MergeCriteria Name = (base, tomerge) -> {
        List<Element> children = base.getChildren(tomerge.getName());
        return children.isEmpty() ? null : children.get(0);
    };
    private static final MergeCriteria Name_Attribute = (base, tomerge) -> {
        List<Element> children = base.getChildren(tomerge.getName());
        if (children.isEmpty())
            return null;
        for (Element child : children) {
            if (equalAttributes(child, tomerge))
                return child;
        }
        return null;
    };
    private static final MergeCriteria Name_Attribute_Content = (Element base, Element tomerge) -> {
        List<Element> children = base.getChildren(tomerge.getName());
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
        List<Attribute> attributes1 = element1.getAttributes();
        List<Attribute> attributes2 = element2.getAttributes();
        if (attributes1.size() != attributes2.size())
            return false;
        for (Attribute attribute : attributes1) {
            if (!element2.getAttributeValue(attribute.getName()).equals(attribute.getValue()))
                return false;
        }
        return true;
    }

    protected static boolean equalsContent(Element element1, Element element2) {
        XMLOutputter out = new XMLOutputter();
        return out.outputString(element1).equals(out.outputString(element2));
    }
}
