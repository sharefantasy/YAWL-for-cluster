package org.scheduleModule.service;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.scheduleModule.service.merge.Combine;
import org.scheduleModule.service.merge.NodeName;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MergeService {

    private Rules rules = Rules.getInstance();

    public String merge(List<String> results, String mergeRules) {

        if (results.size() < 2) {
            return results.get(0);
        }

        ActSpec _mergeRules = rules.containsKey(mergeRules) ?
                rules.get(mergeRules) :
                new ActSpec("response", Combine.getInstance(), NodeName.getInstance());

        Element allDocs = new Element("temp");
        for (String result : results) {
            Document doc = SchedulerUtils.stringToDocument(result);
            allDocs.addContent(doc.getRootElement().clone());
        }

        r_merge(allDocs, _mergeRules);

        Document result = allDocs.getChild("response") == null ?
                new Document(allDocs.getChildren().get(0).clone()) :
                new Document(allDocs.getChild("response").clone());

        return SchedulerUtils.DocumentToString(result);
    }

    private void r_merge(Element base, ActSpec _mergeRules) {
        if (base.getChildren().isEmpty())
            return;

        for (Content child : base.removeContent()) {
            if (child instanceof Element) {
                mergeElementIntoBase(base, (Element) child, _mergeRules);
            }
        }

        for (Element child : base.getChildren()) {
            r_merge(child, _mergeRules);
        }
    }

    private void mergeElementIntoBase(Element base, Element childToMerge, ActSpec _mergeRules) {
        MergeRule rule = _mergeRules.containsKey(childToMerge.getName())
                ? _mergeRules.get(childToMerge.getName())
                : MergeRule.DEFAULT_RULE;
        rule.mergeAction.act(base, childToMerge, rule.mergeCriteria);
    }
}
