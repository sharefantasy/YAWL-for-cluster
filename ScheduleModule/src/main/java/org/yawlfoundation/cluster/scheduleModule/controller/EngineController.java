package org.yawlfoundation.cluster.scheduleModule.controller;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by fantasy on 2016/5/30.
 */
@Controller
@RequestMapping(("/engine"))
public class EngineController {
    @Autowired
    private EngineRepo engineRepo;
    @Autowired
    private TenantRepo tenantRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    String list() {
        StringBuffer stringBuffer = new StringBuffer();
        engineRepo.findAll()
                .forEach(e -> stringBuffer.append(e).append(","));
        return stringBuffer.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {
        modelMap.addAttribute("engine", new Engine());
        return "createEngine";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String create(Engine engine) {
        engine = engineRepo.save(engine);
        return engine.toString();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam("eid") String eid) {
        engineRepo.delete(eid);
        return "redirect:/engine/";
    }
}
