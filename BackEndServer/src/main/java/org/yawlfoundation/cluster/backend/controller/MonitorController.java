package org.yawlfoundation.cluster.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yawlfoundation.cluster.backend.service.monitor.EngineVO;
import org.yawlfoundation.cluster.backend.service.monitor.MonitorService;
import org.yawlfoundation.cluster.backend.service.monitor.ResourceStat;

/**
 * Created by fantasy on 2016/8/4.
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    private final MonitorService monitorService;

    @Autowired
    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private
    @ResponseBody
    ResourceStat list() {
        return monitorService.list();
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
    private
    @ResponseBody
    String shutdown(@RequestParam("engine") String engineID) {
        EngineVO engineVO = new EngineVO();
        engineVO.setEngine_id(engineID);
        return monitorService.shutdown("", engineVO);
    }

    @RequestMapping(value = "/migrate", method = RequestMethod.POST)
    private
    @ResponseBody
    String migrate(@RequestParam("engine") String engineID, @RequestParam("role") String role) {
        EngineVO engineVO = new EngineVO();
        engineVO.setEngine_id(engineID);
        return monitorService.migrate("", engineVO, role);
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    private
    @ResponseBody
    String restore(@RequestParam("engine") String engineID) {
        EngineVO engineVO = new EngineVO();
        engineVO.setEngine_id(engineID);
        return monitorService.restore("", engineVO);
    }

    @RequestMapping(value = "/exile", method = RequestMethod.POST)
    private
    @ResponseBody
    String exile(@RequestParam("engine") String engineID) {
        EngineVO engineVO = new EngineVO();
        engineVO.setEngine_id(engineID);
        return monitorService.exile("", engineVO);
    }
}
