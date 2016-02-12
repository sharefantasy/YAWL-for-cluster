package cluster.gateway.controller;

import cluster.PersistenceManager;
import cluster.entity.Tenant;
import cluster.gateway.service.EngineRoleService;
import cluster.gateway.service.TenantService;
import org.apache.log4j.Logger;
import org.bouncycastle.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yawlfoundation.yawl.util.HibernateEngine;

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

    @RequestMapping(method = RequestMethod.GET)
    public String manager(ModelMap model) {
        List<Tenant> tenants = tenantService.findAllTenant();
        model.addAttribute("tenants", tenants);
        model.addAttribute("newTenant", new Tenant());
        return "tenantManager";
    }

    @RequestMapping(value = "/{tid}/", method = RequestMethod.GET)
    public String detail(@PathVariable long tid, ModelMap model) {
        Tenant tenant = tenantService.getTenantById(tid);
        model.addAttribute("tenant", tenant);
        return "tenantDetail";
    }

    @RequestMapping(value = {"/", "/create/"}, method = RequestMethod.POST)
    public String create(Tenant newTenant, ModelMap model) {
        Tenant t = tenantService.createTenant(newTenant.getName(), newTenant.getSLOspeed());
        return String.format("redirect:/page/tenant/%d/", t.getId());
    }

    @RequestMapping(value = "/update/", method = RequestMethod.POST)
    public String update(Tenant tenant, ModelMap model) {
        tenantService.save(tenant);
        return String.format("redirect:/page/tenant/%d/", tenant.getId());
    }
}
