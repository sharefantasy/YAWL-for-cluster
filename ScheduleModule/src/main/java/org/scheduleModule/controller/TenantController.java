package org.scheduleModule.controller;

import org.scheduleModule.entity.Tenant;
import org.scheduleModule.entity.User;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.SpecRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.repo.UserRepo;
import org.scheduleModule.service.translate.RequestTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    private CaseRepo caseRepo;
    @Autowired
    private SpecRepo specRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public
    @ResponseBody
    String list() {
        List<Tenant> tenants = tenantRepo.findAll();
        StringBuilder sb = new StringBuilder();
        for (Tenant t : tenants) {
            sb.append(",").append(t);
        }
        return sb.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {

        return "createTenant";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(String name, long engineNum) {
        Tenant tenant = new Tenant();
        tenant.setName(name);

        User user = new User();
        tenant.getUserSet().add(user.getId());
        user.setOwner(tenant);
        user.setUserName(name);
        user.setPassword(name);
        tenantRepo.save(tenant);
        userRepo.save(user);
        return "createEngine";
    }

    @RequestMapping(value = "/delete/{tid}", method = RequestMethod.GET)
    public
    @ResponseBody
    String delete(@PathVariable("tid") String tid) {
        tenantRepo.delete(tid);
        return "successful";
    }
}
