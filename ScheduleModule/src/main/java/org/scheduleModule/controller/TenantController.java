package org.scheduleModule.controller;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.entity.User;
import org.scheduleModule.repo.*;
import org.scheduleModule.service.translate.RequestTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/5/14.
 */
@Controller
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EngineRepo engineRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Tenant> list() {
        return tenantRepo.findAll();
    }

    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    public
    @ResponseBody
    Tenant find(@PathVariable String tid) {
        return tenantRepo.findOne(tid);
    }
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {
        modelMap.addAttribute("newTenant", new Tenant());
        modelMap.addAttribute("tenants", tenantRepo.findAll());
        modelMap.addAttribute("engines", engineRepo.findAll().stream().map(Engine::getId).collect(Collectors.toList()));
        return "createTenant";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(Tenant tenant) {
        User user = new User();
        tenant.getUserSet().add(user.getId());
        user.setOwner(tenant);
        user.setUserName(tenant.getName());
        user.setPassword(tenant.getName());
        tenantRepo.save(tenant);
        userRepo.save(user);
        return "redirect:/tenant/" + tenant.getId();
    }

    @RequestMapping(value = "/delete/{tid}", method = RequestMethod.GET)
    public
    @ResponseBody
    String delete(@PathVariable("tid") String tid) {
        tenantRepo.delete(tid);
        return "delete successful";
    }
}
