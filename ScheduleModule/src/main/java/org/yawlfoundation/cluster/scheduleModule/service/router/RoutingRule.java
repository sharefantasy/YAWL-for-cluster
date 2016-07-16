package org.yawlfoundation.cluster.scheduleModule.service.router;

import org.apache.log4j.Logger;
import org.yawlfoundation.cluster.scheduleModule.entity.Engine;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.repo.CaseRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.EngineRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.TenantRepo;
import org.yawlfoundation.cluster.scheduleModule.service.ConnectionService;
import org.yawlfoundation.cluster.scheduleModule.service.translate.RequestTranslator;
import org.yawlfoundation.cluster.scheduleModule.service.translate.ResponseTranslator;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;


/**
 * Created by fantasy on 2016/6/15.
 */
public abstract class RoutingRule {
    @Autowired
    protected TenantRepo tenantRepo;
    @Autowired
    protected CaseRepo caseRepo;
    @Autowired
    protected EngineRepo engineRepo;
    @Autowired
    protected ResponseTranslator responseTranslator;
    @Autowired
    protected RequestTranslator requestTranslator;
    @Autowired
    protected ConnectionService connectionService;

    protected static final Logger _logger = Logger.getLogger(RoutingRule.class);

    public abstract String send(Tenant tenant, Map<String, String> params, String interfce);

    protected String sendWithSessionRetry(Engine e, Map<String, String> params, String interfce) {
        params = requestTranslator.publicToInternal(params, e);
        String result = sendUntranslated(e, params, interfce);
        return responseTranslator.internalToPublic(result, e);
    }

    protected String sendUntranslated(Engine e, Map<String, String> params, String interfce) {
        String result;
        if (params.containsKey("sessionHandle")) {
            params.replace("sessionHandle", connectionService.getSession(e));
        } else {
            params.put("sessionHandle", connectionService.getSession(e));
        }
        try {
            result = connectionService.forward(e, params, interfce);
			if (SchedulerUtils.isInvalidAction(result)) {
                params.replace("sessionHandle", connectionService.getSessionOnline(e));
                result = connectionService.forward(
                        String.format("http://%s:%s/yawl/%s", e.getAddress(), e.getPort(), interfce), params);
            }
        } catch (IOException e1) {
            _logger.error("cannot connect engine(+" + e.getId() + ")");
            return SchedulerUtils.failure("fail to connect");
        }
        return result;
    }
}
