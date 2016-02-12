package cluster.gateway.controller;

import cluster.PersistenceManager;
import cluster.entity.Host;
import cluster.gateway.service.HostService;
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
@RequestMapping("/host")
public class HostController {
    private static final Logger _logger = Logger.getLogger(HostController.class);

    @Autowired
    private HostService hostService;

    @RequestMapping(method = RequestMethod.GET)
    public String Manage(ModelMap model) {
        List<Host> hosts = hostService.getAllHosts();
        Host newHost = new Host();
        model.addAttribute("newHost", newHost);
        model.addAttribute("hosts", hosts);

        return "hostManager";
    }

    @RequestMapping(value = "/{hid}/", method = RequestMethod.GET)
    public String hostDetail(@PathVariable long hid, ModelMap model) {
        Host host = hostService.getHostById(hid);
        model.addAttribute("host", host);
        return "hostdetail";
    }

    @RequestMapping(value = "/reload/")
    public String reloadHosts() {
        hostService.reloadHosts();
        return "redirect:/page/host/";
    }

    @RequestMapping(value = "/reset/{hid}/", method = RequestMethod.POST)
    public String resetCapability(@PathVariable long hid, int engineNum, double capability, ModelMap model) {
        Host host = hostService.getHostById(hid);
        hostService.setHostCapability(hid, engineNum, capability);
        return "redirect:/page/host/" + hid;
    }
}
