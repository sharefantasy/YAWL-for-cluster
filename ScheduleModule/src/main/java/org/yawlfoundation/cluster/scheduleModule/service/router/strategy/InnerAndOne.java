package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yawlfoundation.cluster.scheduleModule.entity.Case;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;

import java.util.Map;

/**
 * Created by fantasy on 2016/7/1.
 */
@Component
public class InnerAndOne extends RoutingRule {
	protected static InnerAndOne instance = new InnerAndOne();

	public static InnerAndOne getInstance() {
		return instance;
	}

	@Autowired
	private OneEngineByCaseOrWorkitem oneEngineByCaseOrWorkitem;
	@Autowired
	private CaseRepo caseRepo;

	@Override
	public String send(Tenant tenant, Map<String, String> params, String interfce) {
		String result = null;
		switch (params.get("action")) {
			case "cancelCase" :
				Case c = caseRepo.findOne(params.get("caseID"));
				if (c == null) {
					return SchedulerUtils.failure("no such case");
				}
				result = oneEngineByCaseOrWorkitem.send(tenant, params, interfce);
				caseRepo.delete(c);
				break;
		}
		return result;
	}
}
