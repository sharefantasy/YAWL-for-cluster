package org.yawlfoundation.cluster.scheduleModule.service;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yawlfoundation.cluster.scheduleModule.service.merge.ActSpec;
import org.yawlfoundation.cluster.scheduleModule.service.merge.MergeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MergeService {
    @Autowired
    private Rules rules;

    public Document merge(List<String> results, String mergeRules) {
        if (results.size() < 2) {
            try {
                return DocumentHelper.parseText(results.get(0));
            } catch (DocumentException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("temp"));
        for (String r : results) {
            Element e;
            try {
                e = DocumentHelper.parseText(r).getRootElement();
            } catch (DocumentException e1) {
                e1.printStackTrace();
                return null;
            }
            doc.getRootElement().add(e.createCopy());
        }


        r_merge(doc.getRootElement(), rules.get(mergeRules));

        Element element;
        element = doc.elementByID("response") == null
                ? (Element) doc.getRootElement().elements().get(0)
                : doc.getRootElement().elementByID("response");

        Document result = DocumentHelper.createDocument(element.createCopy());
        return result;
    }

    @SuppressWarnings("unchecked")
    private void r_merge(Element base, ActSpec _mergeRules) {
        if (!base.elements().isEmpty()) {
            for (Element e : (List<Element>) base.elements()) {
                mergeElementIntoBase(base, e, _mergeRules);
            }
            for (Element e : (List<Element>) base.elements()) {
                r_merge(e, _mergeRules);
            }
        }
    }

    private void mergeElementIntoBase(Element base, Element childToMerge, ActSpec _mergeRules) {
        MergeRule rule = _mergeRules.mergeRule.containsKey(childToMerge.getName())
                ? _mergeRules.mergeRule.get(childToMerge.getName())
                : MergeRuleFactory.DEFAULT_MERGE_RULE;
        rule.mergeAction.act(base, childToMerge, rule.mergeCriteria);
    }
}
