package cluster.general.controller;

import cluster.general.entity.EngineRole;
import cluster.general.service.EngineRoleService;
import cluster.general.service.EngineService;
import cluster.general.entity.Engine;
import cluster.util.PersistenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.List;

/**
 * Created by fantasy on 2016/2/19.
 */
@Controller
@RequestMapping("/engine")
public class EngineController {
    @Autowired
    private PersistenceManager _pm;

    @Autowired
    private EngineService engineService;

    @Autowired
    private EngineRoleService engineRoleService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String manager(ModelMap model) {
        List<Engine> engines = engineService.getAllEngines();
        model.addAttribute("availableRoles", engineRoleService.getUnallocateRoles());
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

    @RequestMapping(value = "/{eid}/", method = RequestMethod.GET)
    public String get(@PathVariable long eid, ModelMap model) {
        Engine engine = engineService.getEngineById(eid);
        model.addAttribute("engine", engine);
        model.addAttribute("availableRoles", engineRoleService.getUnallocateRoles());
        return "engineDetail";
    }

    @RequestMapping(value = "/{eid}/", method = RequestMethod.POST)
    public String edit(@PathVariable long eid, Engine engine, ModelMap model) {
        Engine check = engineService.getEngineById(eid);
        if (check != null) {
            engineService.save(engine);
        }
        return "engineDetail";
    }

    @RequestMapping(value = "/delete/{eid}/", method = RequestMethod.GET)
    public String delete(@PathVariable long eid, ModelMap model) {
        Engine engine = (Engine) _pm.get(Engine.class, eid);
        if (engine != null) {
            _pm.exec(engine, HibernateEngine.DB_DELETE, true);
        }

        return "redirect:/page/engine/";
    }
}
