package org.scheduleModule.service;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jdom2.JDOMException;
import org.scheduleModule.entity.*;
import org.scheduleModule.repo.CaseRepo;
import org.scheduleModule.repo.EngineRepo;
import org.scheduleModule.repo.SpecRepo;
import org.scheduleModule.repo.UserRepo;
import org.scheduleModule.service.allocation.AllocationStrategy;
import org.scheduleModule.service.translate.InternalToPublic;
import org.scheduleModule.service.translate.RequestTranslator;
import org.scheduleModule.service.translate.ResponseTranslator;
import org.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 2016/5/24.
 */


@Service
public class SemanticService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SpecRepo specRepo;
    @Autowired
    private CaseRepo caseRepo;
    @Autowired
    private EngineRepo engineRepo;

    @Autowired
    private ResponseTranslator responseTranslator;
    @Autowired
    private RequestTranslator requestTranslator;
    @Autowired
    private MergeService mergeService;

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private AllocationStrategy allocatorService;
    private static final Logger _logger = Logger.getLogger(SemanticService.class);

    public SemanticService() {

    }

    public String connect(String userID, String password) {

        User u = userRepo.findOne(userID);
        if (u != null) {
            if (u.getPassword().equals(password)) {
                return sessionService.connect(u);
            }
        }
        return SchedulerUtils.failure("no such user.id or password is wrong");

    }

    public String checkConnection(String sessionHandle) {
        return (sessionService.checkConnection(sessionHandle))
                ? SchedulerUtils.SUCCESS
                : SchedulerUtils.failure("Invalid or expired session.");
    }

    public String disconnect(String sessionHandle) {
        if (!sessionService.checkConnection(sessionHandle))
            return SchedulerUtils.failure("Invalid or expired session.");

        return sessionService.disconnect(sessionHandle)
                ? SchedulerUtils.SUCCESS
                : SchedulerUtils.failure("Invalid or expired session.");
    }

    public String getList(Tenant tenant) {
        return OneEngine(tenant, new HashMap<String, String>(), "getList");
    }

    public String upload(Tenant tenant, String specXML) {
        List<YSpecification> specifications;
        try {
            specifications = YMarshal.unmarshalSpecifications(specXML);
        } catch (YSyntaxException e) {
            return SchedulerUtils.failure("Inappropriate specification");
        }
        YSpecification spec = specifications.get(0);
        String specid = spec.getID();
        Spec s = new Spec();
        s.setSpecid(specid);
        s.setVersion(spec.getSpecVersion());
        s.setUri(spec.getURI());
        s.setOwner(tenant);
        specRepo.save(s);
        Map<String, String> params = new HashMap<>();
        params.put("specXML", specXML);
        return AllEngineNoReturn(tenant, params, "upload", "ia");

    }

    public String unload(Tenant tenant, String specid, String specversion, String specuri) {
        Spec spec = specRepo.findOne(specid);
        if (spec == null || !spec.getOwner().equals(tenant)) {
            return SchedulerUtils.failure("no such specification");
        }
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specid);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        return AllEngineNoReturn(tenant, params, "unload", "ia");
    }

    public String getAccounts(Tenant tenant) {
        return OneEngine(tenant, new HashMap<String, String>(), "getAccounts");
    }

    public String getClientAccount(Tenant tenant, String userID) {
        Map<String, String> params = new HashMap<>();
        params.put("userID", userID);
        return OneEngine(tenant, params, "getClientAccount");
    }

    public String getPassword(Tenant tenant, String userID) {
        User user = userRepo.findOne(userID);
        if (user != null) {
            return user.getPassword();
        }
        return SchedulerUtils.failure("no such user");
    }

    public String createAccount(Tenant tenant, String userID, String password, String doco) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "createAccount");
        params.put("userID", userID);
        params.put("password", password);
        params.put("doco", doco);
        User user = new User(userID, password, tenant);
        userRepo.save(user);
        return AllEngineNoReturn(tenant, params, "createAccount", "ia");
    }


    public String updateAccount(Tenant tenant, String userID, String password, String doco) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "updateAccount");
        params.put("userID", userID);
        params.put("password", password);
        params.put("doco", doco);
        return AllEngineNoReturn(tenant, params, "updateAccount", "ia");
    }

    public String deleteAccount(Tenant tenant, String userID) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "deleteAccount");
        params.put("userID", userID);
        return AllEngineNoReturn(tenant, params, "deleteAccount", "ia");
    }

    public String newPassword(Tenant tenant, String session, String password) {
        User user = sessionService.getUserBySession(session);
        user.setPassword(password);
        Map<String, String> params = new HashMap<>();
        params.put("action", "newPassword");
        params.put("password", password);
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            try {
                String customSession = connectionService.connectByUser(e, user);
                params.put("sessionHandle", customSession);
                connectionService.forward(e, params, "ia");
            } catch (IOException e1) {
                return SchedulerUtils.failure("inconsistent engine");
            }
        }

        return SchedulerUtils.SUCCESS;
    }

    public String getYAWLServices(Tenant tenant) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "getYAWLServices");
        return OneEngine(tenant, params, "getYAWLServices");
    }

    public String newYAWLService(Tenant tenant, String serviceStr) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "getYAWLServices");
        params.put("serviceStr", serviceStr);
        return AllEngineNoReturn(tenant, params, "newYAWLService", "ia");
    }

    public String removeYAWLService(Tenant tenant, String serviceURI) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "removeYAWLService");
        params.put("serviceURI", serviceURI);
        return AllEngineNoReturn(tenant, params, "removeYAWLService", "ia");
    }


    public String checkIsAdmin(Tenant tenant, String sessionHandle) {
        Map<String, String> params = new HashMap<>();
        User u = sessionService.getUserBySession(sessionHandle);
        params.put("userID", u.getUserName());
        params.put("password", u.getPassword());
        return OneEngine(tenant, params, "checkIsAdmin");
    }

    public String getAllRunningCases(Tenant tenant) {
        return AllEngineWithReturn(tenant, "getAllRunningCases", new HashMap<String, String>(), "ib");
    }


    public String getCasesForSpecification(Tenant tenant, String specidentifier, String specversion, String specuri) {
        HashMap<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        return AllEngineWithReturn(tenant, "getCasesForSpecification", params, "ib");
    }

    public String getCaseState(Tenant tenant, String caseID) {
        HashMap<String, String> params = new HashMap<>();
        params.put("caseID", caseID);
        return OneEngine(tenant, params, "getCaseState");
    }

    public String getCaseData(String caseID) {

        return OneEngineByCaseID("getCaseData", caseID, "ib");
    }



    public String getCaseInstanceSummary(Tenant tenant) {
        return AllEngineWithReturn(tenant, "getCaseInstanceSummary", new HashMap<String, String>(), "ib");
    }

    public String getWorkItemInstanceSummary(String caseID) {
        return OneEngineByCaseID("getWorkItemInstanceSummary", caseID, "ib");
    }

    public String getParameterInstanceSummary(String caseID) {
        return OneEngineByCaseID("getParameterInstanceSummary", caseID, "ib");
    }

    public String launchCase(Tenant tenant,
                             String specidentifier, String specversion, String specuri,
                             String caseParams, String logData, String mSec,
                             String start, String wait) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "launchCase");
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        params.put("caseParams", caseParams);
        params.put("logData", logData);
//        params.put("mSec",mSec);
//        params.put("start",start);
//        params.put("wait",wait);
        Engine engine = allocatorService.allocate(tenant);
        return sendWithSessionRetry(params, engine, "ib");
    }

    public String cancelCase(String caseID) {
        return OneEngineByCaseID("cancelCase", caseID, "ib");
    }

    public String checkAddInstanceEligible(String workItemID) {
        Map<String, String> params = new HashMap<>();
        return OneEngineByWorkitemID("checkAddInstanceEligible", workItemID, params);
    }



    private String getCaseId(String workItemID) {
        if (workItemID.contains(":")) {
            int delim1 = workItemID.indexOf(":");
            return workItemID.substring(0, delim1);
        }
        return null;
    }

    public String getLiveItems(Tenant tenant) {
        return AllEngineWithReturn(tenant, "getLiveItems", new HashMap<String, String>(), "ib");
    }

    public String getWorkItemsWithIdentifier(Tenant tenant, String id, String idtype) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("idtype", idtype);
        return AllEngineWithReturn(tenant, "getWorkItemsWithIdentifier", params, "ib");
    }

    public String getWorkItemsForService(Tenant tenant, String serviceuri) {
        HashMap<String, String> params = new HashMap<>();
        params.put("serviceuri", serviceuri);
        return AllEngineWithReturn(tenant, "getWorkItemsForService", params, "ib");
    }

    public String getChildren(Tenant tenant, String workItemID) {
        return OneEngineByWorkitemID("getChildren", workItemID, new HashMap<String, String>());
    }

    public String getWorkItemExpiryTime(String workItemID) {
        return OneEngineByWorkitemID("getWorkItemExpiryTime", workItemID, new HashMap<String, String>());
    }

    public String getWorkItem(String workItemID) {
        return OneEngineByWorkitemID("getWorkItem", workItemID, new HashMap<String, String>());
    }

    public String getStartingDataSnapshot(String workItemID) {
        return OneEngineByWorkitemID("getStartingDataSnapshot", workItemID, new HashMap<String, String>());
    }

    public String createInstance(Tenant tenant, String workItemID, String paramValueForMICreation) {
        HashMap<String, String> params = new HashMap<>();
        params.put("paramValueForMICreation", paramValueForMICreation);
        return OneEngineByWorkitemID("createInstance", workItemID, params);
    }

    public String checkout(String workItemID) {
        return OneEngineByWorkitemID("checkout", workItemID, new HashMap<String, String>());
    }

    public String checkin(String workItemID, String data, String logPredicate) {
        HashMap<String, String> params = new HashMap<>();
        params.put("data", data);
        params.put("logPredicate", logPredicate);
        return OneEngineByWorkitemID("checkin", workItemID, params);
    }

    public String rejectAnnouncedEnabledTask(String workItemID) {
        return OneEngineByWorkitemID("rejectAnnouncedEnabledTask", workItemID, new HashMap<String, String>());
    }

    public String startOne(String workitemID) {
        Map<String, String> params = new HashMap<>();
        String caseid = getCaseId(workitemID);
        Case c = caseRepo.findOne(caseid);
        Engine engine;
        if (c != null) {
            engine = c.getEngine();
        } else {
            return SchedulerUtils.failure("no such case");
        }
        params.put("action", "startOne");
        params.put("workitem", workitemID);
        params = requestTranslator.publicToInternal(params, engine);
        String newID = params.get("workitem");
        params.put("user", newID);
        params.remove("workitem");
        String result = sendWithSessionRetry(params, engine, "ib");
        return responseTranslator.internalToPublic(result, engine);
    }

    public String suspend(String workItemID) {
        return OneEngineByWorkitemID("suspend", workItemID, new HashMap<String, String>());
    }

    public String rollback(String workItemID) {
        return OneEngineByWorkitemID("rollback", workItemID, new HashMap<String, String>());
    }

    public String unsuspend(String workItemID) {
        return OneEngineByWorkitemID("unsuspend", workItemID, new HashMap<String, String>());
    }

    public String skip(String workItemID) {
        return OneEngineByWorkitemID("skip", workItemID, new HashMap<String, String>());
    }

    public String getSpecificationPrototypesList(Tenant tenant) {
        return OneEngine(tenant, new HashMap<String, String>(), "getSpecificationPrototypesList");
    }

    public String getSpecificationForCase(String caseId) {
        return OneEngineByCaseID("getSpecificationForCase", caseId, "ib");
    }

    public String getSpecification(Tenant tenant, String specidentifier, String specversion, String specuri) {
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        return OneEngine(tenant, params, "getSpecification");
    }

    public String getSpecificationDataSchema(Tenant tenant, String specidentifier, String specversion, String specuri) {
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        return OneEngine(tenant, params, "getSpecificationDataSchema");
    }

    public String taskInformation(Tenant tenant, String specidentifier, String specversion, String specuri, String taskId) {
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        params.put("taskId", taskId);
        return OneEngine(tenant, params, "taskInformation");
    }

    public String getMITaskAttributes(Tenant tenant, String specidentifier, String specversion, String specuri, String taskId) {
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        params.put("taskId", taskId);
        return OneEngine(tenant, params, "getMITaskAttributes");
    }

    public String getResourcingSpecs(Tenant tenant, String specidentifier, String specversion, String specuri, String taskId) {
        Map<String, String> params = new HashMap<>();
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        params.put("taskId", taskId);
        return OneEngine(tenant, params, "getResourcingSpecs");
    }
//    public String announceEngineInitialised(String eid){Engine engine = engineRepo.findOne(eid);}

    public String announceCaseStarted(Engine engine, String caseID, String launchingService, String delayed,
                                      String specidentifier, String specversion, String specuri) {
        Map<String, String> params = new HashMap<>();
        InternalToPublic i2p = new InternalToPublic();
        params.put("caseID", i2p.Case(caseID, engine));
        params.put("launchingService", launchingService);
        params.put("delayed", delayed);
        params.put("specidentifier", specidentifier);
        params.put("specversion", specversion);
        params.put("specuri", specuri);
        params.put("action", "announceCaseStarted");
        String result = null;
        try {
            result = connectionService.forward(engine.getTenant().getDefaultWorklist(), params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Case c = new Case();
        c.setEngine(engine);
        c.setInternalId(caseID);
        c.setTenant(engine.getTenant());
        caseRepo.save(c);
        return responseTranslator.publicToInternal(result, engine);
    }

    public String announceCaseCompleted(Engine engine, String caseID, String casedata) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "announceCaseCompleted");
        params.put("caseID", caseID);
        params.put("casedata", casedata);
        String result = null;
        try {
            result = connectionService.forward(engine.getTenant().getDefaultWorklist(), params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        caseRepo.delete(caseID);
        return responseTranslator.publicToInternal(result, engine);
    }

    public String announceCompletion(Engine engine, String caseID, String casedata) {
        return announceCaseCompleted(engine, caseID, casedata);
    }

    public String announceCaseCancelled(Engine engine, String caseID) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "announceCaseCancelled");
        params.put("caseID", caseID);
        String result = null;
        try {
            result = connectionService.forward(engine.getTenant().getDefaultWorklist(), params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        caseRepo.delete(caseID);
        return responseTranslator.publicToInternal(result, engine);
    }

    public String announceCaseDeadlocked(Engine engine, String caseID, String tasks) {
        Map<String, String> params = new HashMap<>();
        params.put("tasks", tasks);
        return sendToDefaultRSByCase(engine, caseID, "announceCaseDeadlocked", params);
    }

    public String announceCaseSuspending(Engine engine, String caseID) {
        return sendToDefaultRSByCase(engine, caseID, "announceCaseSuspending", new HashMap<String, String>());
    }



    public String announceCaseSuspended(Engine engine, String caseID) {
        return sendToDefaultRSByCase(engine, caseID, "announceCaseSuspended", new HashMap<String, String>());
    }

    public String announceCaseResumed(Engine engine, String caseID) {
        return sendToDefaultRSByCase(engine, caseID, "announceCaseResumed", new HashMap<String, String>());
    }

    public String announceItemEnabled(Engine engine, String workItem) {
        Document doc = null;
        try {
            doc = SchedulerUtils.stringToDocument(workItem);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return SchedulerUtils.wrap("invalid action");

        }
        String caseID = ((Element) doc.selectNodes("/workItem/caseid").get(0)).getText();
        Case c = caseRepo.findOne(caseID);
        if (c != null) {
            Map<String, String> params = new HashMap<>();
            params.put("workItem", workItem);
            params = requestTranslator.internalToPublic(params, engine);
            return sendToDefaultRSByCase(engine, caseID, "announceItemEnabled", params);
        }
        return SchedulerUtils.failure("workitem inconsistent");
    }

    public String announceItemStatus(Engine engine, String workItem, String oldStatus, String newStatus) {
        Document doc = null;
        try {
            doc = SchedulerUtils.stringToDocument(workItem);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        String caseID = ((Element) doc.selectNodes("/workItem/caseid").get(0)).getText();
        Case c = caseRepo.findOne(caseID);
        if (c != null) {
            Map<String, String> params = new HashMap<>();
            params.put("workItem", workItem);
            params.put("oldStatus", oldStatus);
            params.put("newStatus", newStatus);
            params = requestTranslator.internalToPublic(params, engine);
            return sendToDefaultRSByCase(engine, caseID, "announceItemStatus", params);
        }
        return SchedulerUtils.failure("workitem inconsistent");
    }

    public String announceItemCancelled(Engine engine, String workItem) {
        Document doc = null;
        try {
            doc = SchedulerUtils.stringToDocument(workItem);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String caseID = JDOMUtil.selectElement(doc, "/workItem/caseid").getText();
        Case c = caseRepo.findOne(caseID);
        if (c != null) {
            Map<String, String> params = new HashMap<>();
            params.put("workItem", workItem);
            params = requestTranslator.internalToPublic(params, engine);
            return sendToDefaultRSByCase(engine, caseID, "announceItemCancelled", params);
        }
        return SchedulerUtils.failure("workitem inconsistent");
    }

    public String cancelWorkItem(Engine engine, String workItem) {
        return announceItemCancelled(engine, workItem);
    }

    public String announceTimerExpiry(Engine engine, String workItem) {
        Document doc = null;
        try {
            doc = SchedulerUtils.stringToDocument(workItem);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String caseID = JDOMUtil.selectElement(doc, "/workItem/caseid").getText();
        Case c = caseRepo.findOne(caseID);
        if (c != null) {
            Map<String, String> params = new HashMap<>();
            params.put("workItem", workItem);
            params = requestTranslator.internalToPublic(params, engine);
            return sendToDefaultRSByCase(engine, caseID, "announceTimerExpiry", params);
        }
        return SchedulerUtils.failure("workitem inconsistent");
    }

    public String timerExpiry(Engine engine, String workItem) {
        return announceTimerExpiry(engine, workItem);
    }


    private String AllEngineWithReturn(Tenant tenant, String name, Map<String, String> params, String interfce) {
        List<String> results = new ArrayList<>();
        params.put("action", name);
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            String session = connectionService.getSession(e);
            params.put("sessionHandle", session);
            try {
                String result = connectionService.forward(e, params, interfce);

//                result = responseTranslator.internalToPublic(result,e);
                if (!SchedulerUtils.isInvalidSession(result)) {
                    results.add(result);
                }
            } catch (IOException e1) {
                _logger.warn("inconsistent error");
            }
            params.remove("sessionHandle");
        }
        return mergeService.merge(results, name);
    }

    private String AllEngineNoReturn(Tenant tenant, Map<String, String> params, String action, String interfce) {
        params.put("action", action);
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            params.put("sessionHandle", connectionService.getSession(e));
            String result = sendWithSessionRetry(params, e, interfce);
            if (!result.equals(SchedulerUtils.SUCCESS)) {
                return SchedulerUtils.failure("inconsistent post");
            }
            params.remove("sessionHandle");
        }
        return SchedulerUtils.SUCCESS;
    }

    private String OneEngine(Tenant tenant, Map<String, String> params, String name) {
        params.put("action", name);
        for (String s : tenant.getEngineSet()) {
            Engine e = engineRepo.findOne(s);
            params.put("sessionHandle", connectionService.getSession(e));
            String result = sendWithSessionRetry(params, e, "ia");
            if (result != null) {
                return result;
            }
            params.remove("sessionHandle");
        }
        return SchedulerUtils.failure("cannot get connect to any engine");
    }

    private String OneEngineByCaseID(String name, String caseID, String interfce) {
        Engine engine;
        Case c = caseRepo.findOne(caseID);
        if (c != null) {
            engine = c.getEngine();
        } else {
            return SchedulerUtils.failure("invalid case");
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("action", name);
        params.put("caseID", responseTranslator.publicToInternal(caseID, engine));
        String session = connectionService.getSession(engine);
        params.put("sessionHandle", session);
        String result = sendWithSessionRetry(params, engine, interfce);
        return responseTranslator.internalToPublic(result, engine);
    }

    private String OneEngineByWorkitemID(String name, String workItemID, Map<String, String> params) {
        String caseid = getCaseId(workItemID);
        Case c = caseRepo.findOne(caseid);
        Engine engine;
        if (c != null) {
            engine = c.getEngine();
        } else {
            return SchedulerUtils.failure("no such case");
        }
        params.put("action", name);
        params.put("workItemID", workItemID);
        params = requestTranslator.publicToInternal(params, engine);
        String result = sendWithSessionRetry(params, engine, "ib");
        return responseTranslator.internalToPublic(result, engine);
    }

    private String sendWithSessionRetry(Map<String, String> params, Engine e, String interfce) {
        try {
            String result = connectionService.forward(e, params, interfce);
            if (SchedulerUtils.isInvalidSession(result)) {
                params.remove("sessionHandle");
                params.put("sessionHandle", connectionService.getSessionOnline(e));
                result = connectionService.forward(String.format("http://%s:%s/yawl/%s",
                        e.getAddress(), e.getPort(), interfce), params);
            }
            return result;
        } catch (IOException e1) {
            _logger.error("cannot connect engine(+" + e.getId() + ")");
            return SchedulerUtils.failure("fail to connect");
        }
    }

    private String sendToDefaultRSByCase(Engine engine, String caseID, String action, Map<String, String> params) {
        params.put("caseID", caseID);
        params.put("action", action);
        String result = null;
        try {
            result = connectionService.forward(engine.getTenant().getDefaultWorklist(), params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseTranslator.publicToInternal(result, engine);
    }
}
