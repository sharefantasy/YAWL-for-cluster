package org.scheduleModule.service.router;

import org.apache.log4j.Logger;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Response;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.ConnectionService;
import org.scheduleModule.service.translate.RequestTranslator;
import org.scheduleModule.service.translate.ResponseTranslator;
import org.scheduleModule.util.SchedulerUtils;
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
        try {
            params = requestTranslator.publicToInternal(params, e);
            String result = connectionService.forward(e, params, interfce);
            if (SchedulerUtils.isInvalidSession(result)) {
                params.replace("sessionHandle", connectionService.getSessionOnline(e));
                result = connectionService.forward(String.format("http://%s:%s/yawl/%s",
                        e.getAddress(), e.getPort(), interfce), params);
            }

            return responseTranslator.internalToPublic(result, e);
        } catch (IOException e1) {
            _logger.error("cannot connect engine(+" + e.getId() + ")");
            return SchedulerUtils.failure("fail to connect");
        }
    }
}
