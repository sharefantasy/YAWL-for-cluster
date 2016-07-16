package org.yawlfoundation.cluster.scheduleModule.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Snapshot;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.SnapshotRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.yawlfoundation.cluster.scheduleModule.service.ConnectionService;
import org.yawlfoundation.cluster.scheduleModule.service.RoutingRuleFactory;
import org.yawlfoundation.cluster.scheduleModule.service.Rules;
import org.yawlfoundation.cluster.scheduleModule.service.merge.ActSpec;
import org.yawlfoundation.cluster.scheduleModule.service.router.strategy.Inner;
import org.yawlfoundation.cluster.scheduleModule.service.translate.RequestTranslator;
import org.yawlfoundation.cluster.scheduleModule.service.translate.ResponseTranslator;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils.*;

/**
 * Created by fantasy on 2016/6/16.
 */
@Controller
@RequestMapping("/routing")
public class RoutingController {

	private static final Logger _logger = Logger.getLogger(RoutingController.class);
	@Autowired
	private Rules rules;
	@Autowired
	private RoutingRuleFactory routingRuleFactory;

	@Autowired
	private ConnectionService connectionService;
	@Autowired
	private TenantRepo tenantRepo;
	@Autowired
	private EngineRepo engineRepo;

	@Autowired
	private SnapshotRepo snapshotRepo;
	@Autowired
	private Inner inner;
	@Autowired
	private RequestTranslator requestTranslator;
	@Autowired
	private ResponseTranslator responseTranslator;

	@RequestMapping("/{tid}/yawl/{interfce}")
	public @ResponseBody String routeIn(@PathVariable String tid, @PathVariable String interfce,
			HttpServletRequest request, HttpServletResponse response) {
		Tenant tenant = tenantRepo.findOne(tid);
		if (tenant == null) {
			return wrap(failure("no such tenant"));
		}

		Map<String, String> params = convertMap(request.getParameterMap());
		if (!params.containsKey("action")) {
			return wrap(failure("Invalid or expired session."));
		}
		String action = params.get("action");

		_logger.info(action + " " + request.getRequestURI());
		if (!rules.containsKey(action)) {
			return WRAP_INVALIDACTION_EXCEPTION;
		}

		ActSpec actSpec = rules.get(action);
		String result = actSpec.routingRule.equals("none")
				? inner.send(tenant, params, interfce)
				: routingRuleFactory.buildRoutingRule(actSpec.routingRule).send(tenant, params, actSpec.dest);
		result = result.equals(SUCCESS) ? WRAP_SUCCESS : SchedulerUtils.wrap(result);
		return result;
	}

	@RequestMapping("/{tid}/resourceService/{host}/{port}/*")
	public @ResponseBody String routeOut(@PathVariable String tid, @PathVariable String host, @PathVariable long port,
			HttpServletRequest request, HttpServletResponse response) {
		Tenant tenant = tenantRepo.findOne(tid);
		if (tenant == null) {
			return wrap(failure("no such tenant"));
		}

		Map<String, String> params = convertMap(request.getParameterMap());
		if (!params.containsKey("action")) {
			return wrap(failure("Invalid or expired session."));
		}
		System.out.println(params.get("action") + " " + request.getRequestURI());

		Engine engine = engineRepo.findByAddress(host).stream().filter(e -> e.getPort() == port).findFirst().get();

		switch (params.get("action")) {
			case "announceEngineInitialised" :
				return WRAP_SUCCESS;
			case "announceEngineShutdown" :
				// TODO: 2016/7/14 engine.status = shutdown;
				break;
			case "CaseSnapshot" :
				Snapshot snapshot = Snapshot.fromJSON(params.get("snapshot"), engine.getId());
				snapshotRepo.save(snapshot);
				System.out.println(snapshot.toString());
				return WRAP_SUCCESS;
		}

		String result;
		try {
			result = connectionService.forward(tenant.getDefaultWorklist(),
					requestTranslator.internalToPublic(params, engine));
		} catch (IOException e) {
			e.printStackTrace();
			return failure("failed");
		}
		return responseTranslator.publicToInternal(result, engine);
	}
	private Map<String, String> convertMap(Map<String, String[]> parameterMap) {
		HashMap<String, String> result = new HashMap<>();
		parameterMap.entrySet().stream().filter(entry -> entry.getValue().length > 0)
				.forEach(entry -> result.put(entry.getKey(), entry.getValue()[0]));
		return result;
	}
}
