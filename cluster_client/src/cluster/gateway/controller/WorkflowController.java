package cluster.gateway.controller;

import cluster.entity.EngineStatistics;
import cluster.entity.ServiceEntity;
import cluster.gateway.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by fantasy on 2016/2/7.
 */
@Controller
@RequestMapping("/service")
public class WorkflowController {
    @Autowired
    protected WorkflowService workflowService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String manager(ModelMap model) {
        ServiceEntity entity = workflowService.getCurrentService();
        model.addAttribute("entity", entity);
        model.addAttribute("newEntity", new ServiceEntity());
        return "workflow";
    }

    @RequestMapping(value = {"/", "/create"}, method = RequestMethod.POST)
    public String create(ServiceEntity entity) {

        workflowService.shutdownService();
        workflowService.renewService(entity);
        return "redirect:/page/service/";
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String startService() {
        workflowService.startService();
        return "redirect:/page/service/";
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public String shutdown() {
        workflowService.shutdownService();
        return "redirect:/page/service/";
    }
}
