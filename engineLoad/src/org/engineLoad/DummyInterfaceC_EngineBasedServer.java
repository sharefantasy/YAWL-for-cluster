package org.engineLoad;

import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.YHttpServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by fantasy on 2015/8/5.
 */
public class DummyInterfaceC_EngineBasedServer extends YHttpServlet {
    private InterfaceC_EngineBasedClient _client;
    private boolean enableCluster;
    private Logger _logger = Logger.getLogger(DummyInterfaceC_EngineBasedServer.class);
    private String engineID;
    private String password;
    private String selfURI;
    private static final String CLUSTER_NAME = "cluster";
    private static final String CLUSTER_PASSWORD = "cluster";
    private static final String CLUSTER_DOC = "cluster service";

    public void init(ServletConfig config) throws ServletException {
        try {
            //no need to start
            ServletContext context = config.getServletContext();
            enableCluster =
                    context.getInitParameter("EnableClusterService").equalsIgnoreCase("true");
            if (!enableCluster) {
                return;
            }
            engineID = context.getInitParameter("EngineID");
            String clusterManagementURL = context.getInitParameter("ClusterManagementURL");
            password = context.getInitParameter("password");
            selfURI = context.getInitParameter("SelfURI");
            String engineRole;
            String rsURL = context.getInitParameter("DefaultWorklist").replaceFirst("/ib#resource", "/ic");
            _logger.info("dummy ic started");

            //connect to preset cluster management
            if (engineID == null || password == null || clusterManagementURL == null) {
                return;
            }
            _client = new InterfaceC_EngineBasedClient(clusterManagementURL, engineID, password, selfURI, rsURL);
            if ("success".equalsIgnoreCase(_client.connect())) {
                engineRole = _client.getEngineRole();
                if (!engineRole.equals("<response></response>")) {
                    _logger.info("role " + engineRole);
                }
                _client.heartbeat();
            }

        } catch (IOException e) {
            _logger.error("cluster connection refused");
        }


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);                 // redirect all GETs to POSTs
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        OutputStreamWriter outputWriter = ServletUtils.prepareResponse(response);
        outputWriter.write("<response>" + processPostQuery(request) + "</response>");
        outputWriter.flush();
        outputWriter.close();
    }

    private String processPostQuery(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        String sessionHandle = request.getParameter("sessionHandle");
        String action = request.getParameter("action");
        debug(request, "Post");
        if (action != null) {
            if (action.equalsIgnoreCase("restore")) {
                Date date = new Date();
                msg.append((new Date()).getTime() - date.getTime());
                _logger.info("Remote called restore act");
            } else if (action.equalsIgnoreCase("heartbeat")) {
                msg.append("normal");
                _logger.info("Normal heartbeat at " + new Date().toString());
            } else if (action.equalsIgnoreCase("setEngineRole")) {
                msg.append("success");
                String engineRole = request.getParameter("engineRole");
                _logger.info("New engine role '" + engineRole + "'get");
                System.out.println("New engine role '" + engineRole + "'get");
            } else if (action.equalsIgnoreCase("clusterShutdown")) {
                msg.append("success");
                _client.clusterShutdown();
                _logger.info("cluster service shutdown");
            } else if (action.equalsIgnoreCase("getEngineRole")) {
                _log.info("get engine role by " + sessionHandle);
                System.out.println("check session result: " + sessionHandle);
            } else if ("connect".equals(action)) {
                String role = request.getParameter("engineRole");
                engineID = request.getParameter("engineID");
                password = request.getParameter("password");
                int interval = request.getSession().getMaxInactiveInterval();
                msg.append(engineID);
            } else if ("checkConnection".equals(action)) {

                msg.append(engineID);
            } else if ("invite".equalsIgnoreCase(action)) {
                engineID = request.getParameter("engineID");
                password = request.getParameter("password");
                String role = request.getParameter("engineRole");
                String clusterAddress = request.getParameter("clusterAddress");
                if (_client != null) {
                    _client.stopHeartbeat();
                }
                _client = new InterfaceC_EngineBasedClient(clusterAddress, engineID, password, selfURI, null);
                msg.append("success:").append(engineID);
                _logger.info(String.format("invited as %s", engineID));
                System.out.println(String.format("invited as %s", engineID));
                _client.heartbeat();
            }
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

    public void destroy() {
        WorkitemCounter.getInstace().shutdown();
        _logger.info("shutdown counter");
        if (enableCluster) {
            try {
                _client.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.destroy();
    }
}