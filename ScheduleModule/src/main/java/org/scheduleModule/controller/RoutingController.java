package org.scheduleModule.controller;

import org.scheduleModule.entity.Engine;
import org.scheduleModule.entity.Tenant;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.TenantRepo;
import org.scheduleModule.service.ConnectionService;
import org.scheduleModule.service.SemanticService;
import org.scheduleModule.service.translate.RequestTranslator;
import org.scheduleModule.service.translate.ResponseTranslator;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by fantasy on 2016/5/14.
 */
@Controller
@RequestMapping("/")
public class RoutingController {
    @Autowired
    private RequestTranslator requestTranslator;

    @Autowired
    private ResponseTranslator responseTranslator;

    @Autowired
    private TenantRepo tenantRepo;

    @Autowired
    private SemanticService semanticService;
    @Autowired
    private EngineRepo engineRepo;

    @Autowired
    private ConnectionService connectionService;


    @RequestMapping("/ia/{tid}/")
    public
    @ResponseBody
    String interfaceA(@PathVariable String tid, HttpServletRequest request, HttpServletResponse response) {
        Tenant tenant = tenantRepo.findOne(tid);

        String action = request.getParameter("action");
        if (action == null) {
            return SchedulerUtils.failure("no actions");
        }
        String result = null;
        String userid = request.getParameter("userID");

        String sessionHandle = request.getParameter("sessionHandle");

        switch (action) {
            case "connect":
                String password = request.getParameter("password");
                result = semanticService.connect(userid, password);
                break;
            case "checkConnection":
                result = semanticService.checkConnection(sessionHandle);
                break;
            case "disconnect":
                result = semanticService.disconnect(sessionHandle);
                break;
            case "getList":
                result = semanticService.getList(tenant);
                break;
            case "upload":
                String specXML = request.getParameter("specXML");
                result = semanticService.upload(tenant, specXML);
                break;
            case "unload":
                String specid = request.getParameter("specid");
                String specversion = request.getParameter("specversion");
                String specuri = request.getParameter("specuri");
                result = semanticService.unload(tenant, specid, specversion, specuri);
                break;
            case "getAccounts":
                result = semanticService.getAccounts(tenant);
                break;
            case "getClientAccount":
                result = semanticService.getClientAccount(tenant, userid);
                break;
            case "getPassword":
                result = semanticService.getPassword(tenant, userid);
                break;
            case "createAccount":
                password = request.getParameter("password");
                String doco = request.getParameter("doco");
                result = semanticService.createAccount(tenant, userid, password, doco);
                break;
            case "updateAccount":
                password = request.getParameter("password");
                doco = request.getParameter("doco");
                result = semanticService.updateAccount(tenant, userid, password, doco);
                break;
            case "deleteAccount":
                result = semanticService.deleteAccount(tenant, userid);
                break;
            case "newPassword":
                password = request.getParameter("password");
                result = semanticService.newPassword(tenant, sessionHandle, password);
                break;
            case "getYAWLServices":
                result = semanticService.getYAWLServices(tenant);
                break;
            case "newYAWLService":
                String serviceStr = request.getParameter("serviceStr");
                result = semanticService.newYAWLService(tenant, serviceStr);
                break;
            case "removeYAWLService":
                String serviceURI = request.getParameter("serviceURI");
                result = semanticService.removeYAWLService(tenant, serviceURI);
                break;
            default:
                result = SchedulerUtils.failure("unknown actions");
        }

        return SchedulerUtils.wrap(result);
    }

    @RequestMapping("/ib/{tid}/")
    public
    @ResponseBody
    String interfaceB(@PathVariable String tid, HttpServletRequest request, HttpServletResponse response) {
        Tenant tenant = tenantRepo.findOne(tid);

        String action = request.getParameter("action");
        if (action == null) {
            return SchedulerUtils.failure("no actions");
        }
        String result = null;
        String userid = request.getParameter("userID");
        String password = request.getParameter("password");

        String sessionHandle = request.getParameter("sessionHandle");
        String specidentifier = request.getParameter("specidentifier");
        String specversion = request.getParameter("specversion");
        String specuri = request.getParameter("specuri");

        switch (action) {
            case "checkConnection":
                result = semanticService.checkConnection(sessionHandle);
                break;
            case "checkIsAdmin":
                result = semanticService.checkIsAdmin(tenant, sessionHandle);
                break;
            case "connect":
                result = semanticService.connect(userid, password);
                break;
            case "disconnect":
                result = semanticService.disconnect(sessionHandle);
                break;
            case "getAllRunningCases":
                result = semanticService.getAllRunningCases(tenant);
                break;
            case "getCasesForSpecification":
                result = semanticService.getCasesForSpecification(tenant, specidentifier, specversion, specuri);
                break;
            case "getCaseState":
                break;
            case "getCaseData":
                break;
            case "getCaseInstanceSummary":
                break;
            case "getWorkItemInstanceSummary":
                break;
            case "getParameterInstanceSummary":
                break;
            case "launchCase":
                String caseParams = request.getParameter("caseParams");
                String logData = request.getParameter("logData");
                String mSec = request.getParameter("mSec");
                String start = request.getParameter("start");
                String wait = request.getParameter("wait");
                result = semanticService.launchCase(tenant, specidentifier, specversion, specuri, caseParams, logData, mSec, start, wait);
                break;
            case "cancelCase":
                break;
            case "checkAddInstanceEligible":
                break;
            case "getLiveItems":
                break;
            case "getWorkItemsWithIdentifier":
                break;
            case "getWorkItemsForService":
                break;
            case "getChildren":
                break;
            case "getWorkItemExpiryTime":
                break;
            case "getWorkItem":
                break;
            case "getStartingDataSnapshot":
                break;
            case "createInstance":
                break;
            case "checkout":
                break;
            case "checkin":
                break;
            case "rejectAnnouncedEnabledTask":
                break;
            case "startOne":
                String workitemID = request.getParameter("workitemID");
                result = semanticService.startOne(workitemID);
                break;
            case "suspend":
                break;
            case "rollback":
                break;
            case "unsuspend":
                break;
            case "skip":
                break;
            case "getSpecificationPrototypesList":
                break;
            case "getSpecificationForCase":
                break;
            case "getSpecification":
                break;
            case "getSpecificationDataSchema":
                break;
            case "taskInformation":
                break;
            case "getMITaskAttributes":
                break;
            case "getResourcingSpecs":
                break;
            default:
                break;
            case "announceCaseStarted":
                Engine engine = engineRepo.findByAddress(request.getRequestURI());
                if (engine != null) {
                    String caseID = request.getParameter("caseID");
                    String launchingService = request.getParameter("launchingService");
                    String delayed = request.getParameter("delayed");
                    result = semanticService.announceCaseStarted(engine, caseID, launchingService, delayed, specidentifier, specversion, specuri);
                } else {
                    result = SchedulerUtils.failure("no such engine");
                }
                break;
            case "announceCaseCompleted":
                break;
            case "announceCompletion":
                break;
            case "announceCaseCancelled":
                break;
            case "announceCaseDeadlocked":
                break;
            case "announceCaseSuspending":
                break;
            case "announceCaseSuspended":
                break;
            case "announceCaseResumed":
                break;
            case "announceItemEnabled":
                break;
            case "announceItemStatus":
                break;
            case "cancelWorkItem":
                break;
            case "announceItemCancelled":
                break;
            case "announceTimerExpiry":
                break;
            case "timerExpiry":
                break;


        }
        return SchedulerUtils.wrap(result);

    }

}
