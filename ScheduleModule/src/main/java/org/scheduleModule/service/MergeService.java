package org.scheduleModule.service;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.scheduleModule.service.merge.ActSpec;
import org.scheduleModule.service.merge.MergeRule;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MergeService {
    @Autowired
    private Rules rules;

    public String merge(List<String> results, String mergeRules) {
        if (results.size() < 2) {
            return results.get(0);
        }
        List<Content> contents = new ArrayList<>();
        for (String r : results) {
            Content c;
            try {
                c = SchedulerUtils.stringToDocument(r).getRootElement().clone();
            } catch (JDOMException | IOException e) {
                e.printStackTrace();
                return null;
            }
            contents.add(c);
        }
        Element allDocs = (new Element("temp")).addContent(contents);

        r_merge(allDocs, rules.get(mergeRules));

        Document result = allDocs.getChild("response") == null ?
                new Document(allDocs.getChildren().get(0).clone()) :
                new Document(allDocs.getChild("response").clone());

        return SchedulerUtils.documentToString(result);
    }

    private void r_merge(Element base, ActSpec _mergeRules) {
        if (!base.getChildren().isEmpty()) {
            base.removeContent().stream()
                    .filter(child -> child instanceof Element)
                    .forEach(child -> mergeElementIntoBase(base, (Element) child, _mergeRules));
            base.getChildren().stream()
                    .forEach(child -> r_merge(child, _mergeRules));
        }
    }
    private void mergeElementIntoBase(Element base, Element childToMerge, ActSpec _mergeRules) {
        MergeRule rule = _mergeRules.mergeRule.containsKey(childToMerge.getName())
                ? _mergeRules.mergeRule.get(childToMerge.getName())
                : MergeRuleFactory.DEFAULT_MERGE_RULE;
        rule.mergeAction.act(base, childToMerge, rule.mergeCriteria);
    }
}
