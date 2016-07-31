package cluster.hostTester.controller;

import cluster.general.entity.Host;
import cluster.general.service.HostService;
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

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String manager(ModelMap modelMap) {
		List<TestPlanEntity> plans = testPlanService.getAllTestPlan();
		List hosts = hostService.getAllHosts();
		modelMap.addAttribute("plans", plans);
		modelMap.addAttribute("testplan", new TestPlanEntity());
		modelMap.addAttribute("hosts", hosts);
		return "planManager";
	}

	@RequestMapping(value = {"/", "/create"}, method = RequestMethod.POST)
	public String create(TestPlanEntity testPlan) {
		testPlanService.createTestPlan(testPlan.getHost(), testPlan.getEngineNumber(), testPlan.getEndTime());
		return "redirect:/page/testplan/";
	}

	@RequestMapping(value = "/testplan/{pid}/", method = RequestMethod.GET)
	public String get(@PathVariable long pid, ModelMap modelMap) {
		TestPlanEntity entity = testPlanService.getTestPlanByID(pid);
		modelMap.addAttribute("entity", entity);
		return "plandetail";
	}

	@RequestMapping(value = "/start/{tpid}/")
	public String startTestplan(@PathVariable long tpid, ModelMap model) {
		TestPlanEntity tp = testPlanService.getTestPlanByID(tpid);
		testPlanService.startTest(tp, false);
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

	@RequestMapping(value = "/startTestShortcut/{tpid}", method = RequestMethod.GET)
	public String bypass(@PathVariable long tpid, ModelMap modelMap) {
		TestPlanEntity tp = testPlanService.getTestPlanByID(tpid);
		testPlanService.startTest(tp, true);
		return "redirect:/page/testplan/";
	}
}
