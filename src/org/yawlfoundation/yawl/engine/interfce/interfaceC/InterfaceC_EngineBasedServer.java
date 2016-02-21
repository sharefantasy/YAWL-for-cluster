package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.engine.WorkitemCounter;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.YPersistenceManager;
import org.yawlfoundation.yawl.engine.interfce.EngineGateway;
import org.yawlfoundation.yawl.engine.interfce.EngineGatewayImpl;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.YHttpServlet;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;
import org.yawlfoundation.yawl.util.SaxonErrorListener;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EngineBasedServer extends YHttpServlet {
    private EngineGateway _engine;
    private InterfaceC_EngineBasedClient _client;
    private boolean enableCluster;
    private Logger _logger = Logger.getLogger(InterfaceC_EngineBasedServer.class);
    private String engineID;
    private String password;
    private String selfURI;
    private static final String CLUSTER_NAME = "cluster";
    private static final String CLUSTER_PASSWORD = "cluster";
    private static final String CLUSTER_DOC = "cluster service";

    public void init(ServletConfig config) throws ServletException {
        int maxWaitSeconds = 5;
        try {
            //no need to start
            ServletContext context = config.getServletContext();
            enableCluster =
                    context.getInitParameter("EnableClusterService").equalsIgnoreCase("true");
            YPersistenceManager.setIsCluster(enableCluster);


            if (!enableCluster){
                return ;
            }

            // init engine
            _engine = (EngineGateway) context.getAttribute("engine");
            if (_engine == null) {
                String persistOn = context.getInitParameter("EnablePersistence");
                String enableHbnStatsStr = context.getInitParameter("EnableHibernateStatisticGathering");

                boolean persist = persistOn != null && persistOn.equalsIgnoreCase("true");
                boolean enableHbnStats = enableHbnStatsStr != null && enableHbnStatsStr.equalsIgnoreCase("true");
                _engine = new EngineGatewayImpl(persist, enableHbnStats);
                _engine.setActualFilePath(context.getRealPath("/"));
                context.setAttribute("engine", _engine);
            }

            String logging = context.getInitParameter("EnableLogging");
            if (logging != null && logging.equalsIgnoreCase("false")) {
                _engine.disableLogging();
            }
            // read the current version properties
            _engine.initBuildProperties(context.getResourceAsStream(
                    "/WEB-INF/classes/version.properties"));

            int maxWait = StringUtil.strToInt(
                    context.getInitParameter("InitialisationAnnouncementTimeout"), -1);
            if (maxWait >= 0) maxWaitSeconds = maxWait;


            engineID = context.getInitParameter("EngineID");
            String clusterManagementURL = context.getInitParameter("ClusterManagementURL");
            password = context.getInitParameter("password");
            selfURI = context.getInitParameter("SelfURI");
            String engineRole = context.getInitParameter("EngineRole");
            String rsURL = context.getInitParameter("DefaultWorklist").replaceFirst("/ib#resource","/ic");
            YPersistenceManager.setEngineRole(engineRole);
            if (engineID == null || password == null || clusterManagementURL == null) {
                return;
            }
            _client = new InterfaceC_EngineBasedClient(clusterManagementURL, engineID, password, selfURI, rsURL);
            if ("success".equalsIgnoreCase(_client.connect())) {
                engineRole = _client.getEngineRole();
                if (!engineRole.equals("<response></response>")) {
                    _logger.info("role " + engineRole);
                    if (!engineRole.equals("failed")){
                        resetEngineRole(engineRole);
                    }
                }
                _client.heartbeat();
            }

        } catch (YPersistenceException e) {
            _logger.fatal("Failure to initialise runtime (persistence failure)", e);
            throw new UnavailableException("Persistence failure");
        } catch (IOException e) {

            _logger.error("cluster connection refused");
        }

        if (_engine != null) {
            _engine.notifyServletInitialisationComplete(maxWaitSeconds);
        } else {
            _logger.fatal("Failed to initialise Engine (unspecified failure). Please " +
                    "consult the logs for details");
            throw new UnavailableException("Unspecified engine failure");
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);                 // redirect all GETs to POSTs
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        OutputStreamWriter outputWriter = ServletUtils.prepareResponse(response);
        if (_engine.enginePersistenceFailure()) {
            _logger.fatal("************************************************************");
            _logger.fatal("A failure has occurred whilst persisting workflow state to the");
            _logger.fatal("database. Check the status of the database connection defined");
            _logger.fatal("for the YAWL service, and restart the YAWL web application.");
            _logger.fatal("Further information may be found within the Tomcat log files.");
            _logger.fatal("************************************************************");
            response.sendError(500, "Database persistence failure detected");
        }
        outputWriter.write("<response>" + processPostQuery(request) + "</response>");
        outputWriter.flush();
        outputWriter.close();
        //todo find out how to provide a meaningful 500 message in the format of  a fault message.
    }

    private void resetEngineRole(String role) throws YPersistenceException {
        YPersistenceManager.setEngineRole(role);
        YEngine.getInstance().restore();
    }
    private String processPostQuery(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        String sessionHandle = request.getParameter("sessionHandle");
        String action = request.getParameter("action");
        debug(request, "Post");
        String clusterAddress = String.format("http://%s/cluster/service/", request.getRemoteAddr());
        try {
            if (action != null) {
                if (action.equalsIgnoreCase("restore")) {
                    Date date = new Date();
                    _engine.restore(sessionHandle);
                    msg.append((new Date()).getTime() - date.getTime());
                    _logger.info("Remote called restore act");
                }
                else if(action.equalsIgnoreCase("heartbeat")) {
                    msg.append("normal");
                    _logger.info("Normal heartbeat at " + new Date().toString());
                }
                else if(action.equalsIgnoreCase("setEngineRole")) {
                    msg.append("success");
                    String engineRole = request.getParameter("engineRole");
                    resetEngineRole(engineRole);
                    _logger.info("New engine role '" + engineRole + "'get");
                    System.out.println("New engine role '" + engineRole + "'get");
                }
                else if(action.equalsIgnoreCase("clusterShutdown")) {
                    msg.append("success");
                    _client.clusterShutdown();
                    YPersistenceManager.setEngineRole("default");
                    _logger.info("cluster service shutdown");
                }
                else if (action.equalsIgnoreCase("getEngineRole")){

                    if (!_engine.checkConnection(sessionHandle).startsWith("</failure")){
                        msg.append(YPersistenceManager.getEngineRole());
                        _log.info("get engine role by " + sessionHandle);
                    }
                    System.out.println("check session result: " + _engine.checkConnection(sessionHandle));
                }
                else if ("connect".equals(action)) {
                    String role = request.getParameter("engineRole");
                    engineID = request.getParameter("engineID");
                    password = request.getParameter("password");
                    resetEngineRole(role);
                    int interval = request.getSession().getMaxInactiveInterval();
                    msg.append(_engine.connect("cluster", "cluster", interval));
                }
                else if ("checkConnection".equals(action)) {

                    msg.append(_engine.checkConnectionForAdmin(sessionHandle));
                } else if ("invite".equalsIgnoreCase(action)) {
                    engineID = request.getParameter("engineID");
                    password = request.getParameter("password");
                    String role = request.getParameter("engineRole");
                    if (role != null) {
                        if (!role.equals("null")) {
                            resetEngineRole(role);
                        }
                    }
                    if (YEngine.getInstance().getRegisteredYawlService(clusterAddress) == null) {
                        YAWLServiceReference y = new YAWLServiceReference(clusterAddress, null, CLUSTER_NAME, CLUSTER_DOC);
                        YEngine.getInstance().addYawlService(y);
                    }
                    if (YEngine.getInstance().getExternalClient(CLUSTER_NAME) == null) {
                        YExternalClient c = new YExternalClient(CLUSTER_NAME, CLUSTER_PASSWORD, CLUSTER_DOC);
                        YEngine.getInstance().addExternalClient(c);
                    }
                    String session = YEngine.getInstance().getSessionCache().connect(CLUSTER_NAME, CLUSTER_PASSWORD, -1);
                    _client = new InterfaceC_EngineBasedClient(clusterAddress, engineID, "YAWLCLUSTER", selfURI, null);
                    msg.append("success:").append(session);
                }

            }

        } catch (YPersistenceException | RemoteException e) {
            e.printStackTrace();
        }
        if (msg.length() == 0) {
            msg.append("<failure><reason>Invalid action or exception was thrown." +
                    "</reason></failure>");
        }
        return msg.toString();
    }
    private void debug(HttpServletRequest request, String service) {
        _logger.debug("\nInterfaceA_EngineBasedServer::do" + service + "() " +
                "request.getRequestURL = " + request.getRequestURL());
        _logger.debug("\nInterfaceA_EngineBasedServer::do" + service +
                "() request.parameters = ");
        Enumeration paramNms = request.getParameterNames();
        while (paramNms.hasMoreElements()) {
            String name = (String) paramNms.nextElement();
            _logger.debug("\trequest.getParameter(" + name + ") = " +
                    request.getParameter(name));
        }
    }
    public void destroy(){
        if(enableCluster){
            try {
                _client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.destroy();
    }
}