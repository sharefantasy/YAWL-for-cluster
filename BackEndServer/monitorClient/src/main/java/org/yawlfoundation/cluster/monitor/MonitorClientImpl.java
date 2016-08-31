package org.yawlfoundation.cluster.monitor;

import net.dongliu.requests.Requests;
import org.springframework.beans.factory.annotation.Value;
import org.yawlfoundation.cluster.backend.service.monitor.EngineVO;
import org.yawlfoundation.cluster.backend.service.monitor.ResourceStat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fantasy on 2016/8/20.
 */
public class MonitorClientImpl implements MonitorClient {

    private String monitorAddress;

    public MonitorClientImpl(@Value("#service.monitor.address") String monitorAddress) {
        this.monitorAddress = monitorAddress;
    }

    @Override
    public ResourceStat list() {
        return Requests.get(monitorAddress + "/list").send().readAsJson(ResourceStat.class);
    }

    @Override
    public String shutdown(EngineVO engine) {
        Map<String, String> param = new HashMap<>();
        param.put("engine", engine.getEngine_id());
        return Requests.post(monitorAddress + "/shutdown").params(param).send().readToText();
    }

    @Override
    public String migrate(EngineVO engine, String role) {
        Map<String, String> param = new HashMap<>();
        param.put("engine", engine.getEngine_id());
        param.put("role", role);
        return Requests.post(monitorAddress + "/role").params(param).send().readToText();
    }

    @Override
    public String restore(EngineVO engine) {
        Map<String, String> param = new HashMap<>();
        param.put("engine", engine.getEngine_id());
        return Requests.post(monitorAddress + "/restore").params(param).send().readToText();
    }

    @Override
    public String exile(EngineVO engine) {
        Map<String, String> param = new HashMap<>();
        param.put("engine", engine.getEngine_id());
        return Requests.post(monitorAddress + "/exile").params(param).send().readToText();
    }
}
