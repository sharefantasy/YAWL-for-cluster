package org.scheduleModule.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.scheduleModule.service.merge.ActSpec;
import org.scheduleModule.service.router.RoutingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Rules extends HashMap<String, ActSpec> {


    private File[] debug() throws IllegalArgumentException {
        File directory;
        File[] files = null;
        directory = new File("interfaces");
        if (directory.isDirectory()) {
            files = directory.listFiles(pathname -> pathname.getPath().endsWith(".json"));
        }
        return files;
    }

    private File[] release() {
        URL path = Thread.currentThread()
                .getContextClassLoader()
                .getResource("../interfaces");
        File directory;
        File[] files = null;
        if (path != null) {
            directory = new File(path.getFile());
            if (directory.isDirectory()) {
                files = directory.listFiles(pathname -> pathname.getPath().endsWith(".json"));
            }
        }
        return files;
    }

    @SuppressWarnings("unchecked")
    public Rules() {
        File[] files = release();
        if (files == null) {
            files = debug();
        }
        if (files == null) {
            throw new IllegalArgumentException();
        }
        for (File f : files) {
            String jsonfile = getFileString(f);
            if (jsonfile.equals(""))
                continue;
            JSONObject rulesJSON = JSON.parseObject(jsonfile);
            for (Entry<String, Object> ruleString : rulesJSON.entrySet()) {
                ActSpec actSpec = new ActSpec();
                JSONObject ruleMap = (JSONObject) ruleString.getValue();
                if (ruleMap.containsKey("MergeRules")) {
                    JSONObject MergeRules = (JSONObject) ruleMap.get("MergeRules");
                    for (Map.Entry<String, Object> mergeRule : MergeRules.entrySet()) {
                        List<String> r = (List<String>) mergeRule.getValue();
                        actSpec.mergeRule.put(mergeRule.getKey(),
                                MergeRuleFactory.buildMergeRule(r.get(0), r.get(1)));
                    }
                }
                if (ruleMap.containsKey("RoutingRules")) {
                    actSpec.routingRule = (String) ruleMap.get("RoutingRules");
                }
                if (!ruleMap.containsKey("interface"))
                    throw new IllegalArgumentException();

                actSpec.dest = RoutingRuleFactory.getInterfacePath((String) ruleMap.get("interface"));
                put(ruleString.getKey(), actSpec);
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