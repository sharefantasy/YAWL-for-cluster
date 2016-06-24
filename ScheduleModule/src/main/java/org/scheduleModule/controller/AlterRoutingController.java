package org.scheduleModule.controller;

import org.jdom2.Document;
import org.jdom2.Element;
import org.scheduleModule.entity.Case;
import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Response;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.RoutingRuleFactory;
import org.scheduleModule.service.router.strategy.Inner;
import org.scheduleModule.service.ConnectionService;
import org.scheduleModule.service.Rules;
import org.scheduleModule.service.merge.ActSpec;
import org.scheduleModule.service.translate.RequestTranslator;
import org.scheduleModule.service.translate.ResponseTranslator;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fantasy on 2016/6/16.
 */
@Controller
@RequestMapping("/alter")
public class AlterRoutingController {

    @Autowired
    private Rules rules;
    @Autowired
    private RoutingRuleFactory routingRuleFactory;

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private CaseRepo caseRepo;
    @Autowired
    private EngineRepo engineRepo;
    @Autowired
    private Inner inner;
    @Autowired
    private RequestTranslator requestTranslator;
    @Autowired
    private ResponseTranslator responseTranslator;

    @RequestMapping("/{tid}/*/{interfce}")
    public
    @ResponseBody
    String routeIn(@PathVariable String tid, @PathVariable String interfce, HttpServletRequest request, HttpServletResponse response) {
        Tenant tenant = tenantRepo.findOne(tid);
        String result;
        if (tenant == null) {
            return SchedulerUtils.failure("no such tenant");
        }

        Map<String, String> params = convertMap(request.getParameterMap());
        String action = params.get("action");
        if (!rules.containsKey(action))
            return SchedulerUtils.failure("Invalid Action");
        ActSpec actSpec = rules.get(action);

        result = actSpec.routingRule.equals("none")
                ? inner.send(tenant, params, interfce) :
                routingRuleFactory.buildRoutingRule(actSpec.routingRule).send(tenant, params, actSpec.dest);
        return SchedulerUtils.wrap(result);
    }

    @RequestMapping("/{tid}/resourceService/{host}/{port}/*")
    public
    @ResponseBody
    String routeOut(@PathVariable String tid, @PathVariable String host, @PathVariable long port,
                    HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = convertMap(request.getParameterMap());
        if (params.get("action").equals("announceEngineInitialised"))
            return SchedulerUtils.wrap(SchedulerUtils.SUCCESS);

        Tenant tenant = tenantRepo.findOne(tid);
        Engine engine = engineRepo.findByAddress(host).stream()
                .filter(e -> e.getPort() == port).findFirst().get();
        String result;
        try {
            result = connectionService.forward(tenant.getDefaultWorklist(), requestTranslator.internalToPublic(params, engine));
        } catch (IOException e) {
            e.printStackTrace();
            return SchedulerUtils.failure("failed");
        }
        return SchedulerUtils.wrap(responseTranslator.publicToInternal(result, engine));
    }

    private Map<String, String> convertMap(Map<String, String[]> parameterMap) {
        HashMap<String, String> result = new HashMap<>();
        parameterMap.entrySet().stream()
                .filter(entry -> entry.getValue().length > 0)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()[0]));
        return result;
    }
}
