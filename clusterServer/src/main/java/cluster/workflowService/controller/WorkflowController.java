package cluster.workflowService.controller;

import cluster.workflowService.entity.WorkflowPlan;
import cluster.workflowService.service.WorkflowPlanService;
import org.apache.regexp.RE;
import org.jvnet.hk2.internal.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/2/7.
 */
@Controller
@RequestMapping("/workflow")
public class WorkflowController {
	@Autowired
	protected WorkflowPlanService workflowService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String manager(ModelMap model) {
		List<WorkflowPlan> allplans = workflowService.getAllWorkflowPlans();
		model.addAttribute("newplan", new WorkflowPlan());
		model.addAttribute("workingPlan",
				allplans.stream().filter(WorkflowPlan::isWorking).collect(Collectors.toList()));
		model.addAttribute("idlePlan", allplans.stream().filter(w -> !w.isWorking()).collect(Collectors.toList()));
		return "workflowPlanManger";
	}

	@RequestMapping(value = "/{wid}", method = RequestMethod.GET)
	public String getPlan(@PathVariable long wid, ModelMap model) {
		WorkflowPlan plan = workflowService.getWorkflowPlanById(wid);
		model.addAttribute("plan", plan);
		return "workflowPlanDetail";
	}

	@RequestMapping(value = "/{wid}", method = RequestMethod.POST)
	public String editPlan(@PathVariable long wid, WorkflowPlan plan, ModelMap model) {
		workflowService.save(plan);
		return "redirect:/page/workflow/" + wid;
	}

	@RequestMapping(value = {"/", "/create"}, method = RequestMethod.POST)
	public String create(WorkflowPlan plan) {
		WorkflowPlan w = workflowService.getWorkflowPlanById(plan.getId());
		if (w == null) {
			workflowService.save(plan);
		}
		return "redirect:/page/workflow/" + plan.getId();
	}

	@RequestMapping(value = "/start/{wid}", method = RequestMethod.GET)
	public String startService(@PathVariable long wid) {
		workflowService.startPlan(wid);
		return "redirect:/page/workflow/" + wid;
	}

	@RequestMapping(value = "/shutdown/{wid}", method = RequestMethod.GET)
	public String shutdown(@PathVariable long wid) {
		workflowService.shutdown(wid);
		return "redirect:/page/service/";
	}
}
