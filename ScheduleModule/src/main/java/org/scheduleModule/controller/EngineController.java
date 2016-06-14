package org.scheduleModule.controller;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
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
        for (Engine e : engineRepo.findAll()) {
            stringBuffer.append(e);
        }
        return stringBuffer.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {
        modelMap.addAttribute("engine", new Engine());
        modelMap.addAttribute("tenants", tenantRepo.findAll());
        return "create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Engine create(Engine engine) {
        engine = engineRepo.save(engine);
        return engine;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam("eid") String eid) {
        engineRepo.delete(eid);
        return "redirect:/engine/";
    }
}
