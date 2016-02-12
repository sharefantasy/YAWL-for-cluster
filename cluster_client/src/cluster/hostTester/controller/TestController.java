package cluster.hostTester.controller;

import cluster.entity.Host;
import cluster.gateway.service.HostService;
import cluster.hostTester.entity.TestPlanEntity;
import cluster.hostTester.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by fantasy on 2016/1/31.
 */
@Controller
@RequestMapping("/testplan")
public class TestController {

    @Autowired
    private TestPlanService testPlanService;

    @Autowired
    private HostService hostService;

    @RequestMapping(value = "/create_test_plan", method = RequestMethod.GET)
    public String createTestPlan(ModelMap modelMap) {
        List<Host> hosts = hostService.getAllHosts();

        modelMap.addAttribute("testplan", new TestPlanEntity());
        modelMap.addAttribute("hostlist", hosts);
        return "testplan";
    }

    @RequestMapping(value = "/create_test_plan", method = RequestMethod.POST)
    public String confirmPlan(TestPlanEntity testPlan, ModelMap modelMap) {
        testPlanService.createTestPlan(testPlan);

        modelMap.addAttribute("testplan", testPlan);
        return String.format("redirect:/page/testplan/get_test_plan/%d/", testPlan.getId());
    }

    @RequestMapping(value = "/get_test_plan/{pid}/", method = RequestMethod.GET)
    public String getTestPlan(@PathVariable long pid, ModelMap modelMap) {
        TestPlanEntity entity = testPlanService.getTestPlanByID(pid);

        modelMap.addAttribute("entity", entity);
        return "plandetail";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showPlanManager(ModelMap modelMap) {

        List<TestPlanEntity> plans = testPlanService.getAllTestPlan();
        List hosts = hostService.getAllHosts();

        modelMap.addAttribute("plans", plans);
        modelMap.addAttribute("testplan", new TestPlanEntity());
        modelMap.addAttribute("hosts", hosts);
        return "planManager";
    }

    @RequestMapping(value = "/start/{tpid}/")
    public String startTestplan(@PathVariable long tpid, ModelMap model) {
        TestPlanEntity tp = testPlanService.getTestPlanByID(tpid);
        testPlanService.startTest(tp);
        return String.format("redirect:/page/testplan/%d/", tpid);
    }

    @RequestMapping(value = "/shutdown/{tpid}/")
    public String forceShutdownTest(@PathVariable long tpid, ModelMap model) {
        TestPlanEntity tp = testPlanService.getTestPlanByID(tpid);
        testPlanService.forceShutdownTestPlan(tp);
        return "redirect:/page/testplan/";
    }

    @RequestMapping(value = "/shutdown/all/")
    public String forceShutdownAllTest(ModelMap model) {
        testPlanService.forceShutdownAllTestPlan();
        return "redirect:/page/testplan/";
    }

}
