package org.scheduleModule.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.scheduleModule.service.merge.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 2016/6/6.
 */
class MergeRule {
    public MergeAction mergeAction;
    public MergeCriteria mergeCriteria;

    public MergeRule(MergeAction mergeAction, MergeCriteria mergeCriteria) {
        this.mergeAction = mergeAction;
        this.mergeCriteria = mergeCriteria;
    }

    public static final MergeRule DEFAULT_RULE = new MergeRule(Append.getInstance(), NodeName.getInstance());
}

class ActSpec extends HashMap<String, MergeRule> {
    public ActSpec(String name, MergeAction mergeAction, MergeCriteria mergeCriteria) {
        MergeRule rule = new MergeRule(mergeAction, mergeCriteria);
        put(name, rule);
    }

    public ActSpec() {
    }
}

public class Rules extends HashMap<String, ActSpec> {

    private static final HashMap<String, MergeAction> actions = new HashMap<String, MergeAction>() {{
        put("combine", Combine.getInstance());
        put("append", Append.getInstance());
        put("complement", Complement.getInstance());
    }};
    private static final HashMap<String, MergeCriteria> criteria = new HashMap<String, MergeCriteria>() {{
        put("name", NodeName.getInstance());
        put("name_attribute", NodeNameAndAttribute.getInstance());
        put("name_attribute_content", NodeNameAndAttributeAndContent.getInstance());
    }};
    private static final Rules instance = new Rules();

    public static Rules getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    private Rules() {
        File directory = new File("interfaces");
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        for (File f : files) {
            JSONObject rulesJSON = JSON.parseObject(getFileString(f));
            for (Entry<String, Object> ruleString : rulesJSON.entrySet()) {
                ActSpec actSpec = new ActSpec();
                JSONObject ruleMap = (JSONObject) ruleString.getValue();
                if (ruleMap.containsKey("MergeRules")) {
                    JSONObject MergeRules = (JSONObject) ruleMap.get("MergeRules");
                    for (Map.Entry<String, Object> mergeRule : MergeRules.entrySet()) {
                        List<String> r = (List<String>) mergeRule.getValue();
                        actSpec.put(mergeRule.getKey(),
                                new MergeRule(actions.get(r.get(0)),
                                        criteria.get(r.get(1))));
                    }
                    put(ruleString.getKey(), actSpec);
                }
            }
        }
    }

    private String getFileString(File file) {
        StringBuilder sb = new StringBuilder();
        if (file != null) {
            try {
                FileInputStream io = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(io));
                String line;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }
}