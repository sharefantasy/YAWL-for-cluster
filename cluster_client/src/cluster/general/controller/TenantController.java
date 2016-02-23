package cluster.general.controller;

import cluster.general.entity.Tenant;
import cluster.general.service.TenantService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by fantasy on 2016/2/6.
 */
@Controller
@RequestMapping(value = "/tenant")
public class TenantController {
    private static final Logger _logger = Logger.getLogger(TenantController.class);


    @Autowired
    private TenantService tenantService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String manager(ModelMap model) {
        List<Tenant> tenants = tenantService.findAllTenant();
        model.addAttribute("tenants", tenants);
        model.addAttribute("newTenant", new TenantInfo());
        return "tenantManager";
    }

    @RequestMapping(value = "/{tid}/", method = RequestMethod.GET)
    public String detail(@PathVariable long tid, ModelMap model) {
        Tenant tenant = tenantService.getTenantById(tid);
        model.addAttribute("tenant", tenant);
        return "tenantDetail";
    }

    @RequestMapping(value = {"/", "/create/"}, method = RequestMethod.POST)
    public String create(TenantInfo newTenant, ModelMap model) {
        Tenant t = tenantService.createTenant(newTenant.getName(), newTenant.getSLOspeed(), newTenant.getRoleNum());
        return String.format("redirect:/page/tenant/%d/", t.getId());
    }

    @RequestMapping(value = "/update/", method = RequestMethod.POST)
    public String update(Tenant tenant, ModelMap model) {
        tenantService.save(tenant);
        return String.format("redirect:/page/tenant/%d/", tenant.getId());
    }
}

class TenantInfo {
    private String name;
    private double SLOspeed;
    private int roleNum;

    public double getSLOspeed() {
        return SLOspeed;
    }

    public void setSLOspeed(double SLOspeed) {
        this.SLOspeed = SLOspeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoleNum() {
        return roleNum;
    }

    public void setRoleNum(int roleNum) {
        this.roleNum = roleNum;
    }
}