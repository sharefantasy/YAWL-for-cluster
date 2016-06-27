package org.yawlfoundation.cluster.scheduleModule.controller;

import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.UserRepo;

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
    String find(@PathVariable String tid) {
        return tenantRepo.findOne(tid).toString();
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
