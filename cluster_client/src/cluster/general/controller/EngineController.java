package cluster.general.controller;

import cluster.general.service.EngineService;
import cluster.general.entity.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by fantasy on 2016/2/19.
 */
@Controller
@RequestMapping("/engine")
public class EngineController {

    @Autowired
    private EngineService engineService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String manager(ModelMap model) {
        List<Engine> engines = engineService.getEngines();
        model.addAttribute("engines", engines);
        model.addAttribute("newEngine", new Engine());
        return "engineManager";
    }

    @RequestMapping(value = {"/", "/invite"}, method = RequestMethod.POST)
    public String invite(Engine engine, ModelMap model) {
        engineService.inviteEngine(engine.getAddress()
                , engine.getEngineID()
                , engine.getEngineRole(),
                engine.getIp());
        return "redirect:/page/engine/";
    }

    @RequestMapping(value = "/{eid}", method = RequestMethod.GET)
    public String getEngine(@PathVariable long eid, ModelMap model) {
        Engine engine = engineService.getEngineById(eid);
        model.addAttribute("engine", engine);
        return "engineDetail";
    }
}
