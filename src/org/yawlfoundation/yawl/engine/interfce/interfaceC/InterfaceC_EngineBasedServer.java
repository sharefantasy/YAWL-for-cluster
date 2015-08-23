package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.yawlfoundation.yawl.engine.interfce.EngineGateway;
import org.yawlfoundation.yawl.engine.interfce.EngineGatewayImpl;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.YHttpServlet;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;
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
import java.util.Date;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EngineBasedServer extends YHttpServlet {
    private EngineGateway _engine;
    private InterfaceC_EngineBasedClient _client;
    private boolean enableCluster;
    public void init() throws ServletException {
        int maxWaitSeconds = 5;
        try {
            //no need to start
            ServletContext context = getServletContext();
            enableCluster =
                    context.getInitParameter("EnableClusterService").equalsIgnoreCase("true");
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


            String engineID = context.getInitParameter("EngineID");
            String clusterManagementURL = context.getInitParameter("ClusterManagementURL");
            String identifier = context.getInitParameter("Identifier");
            _client = new InterfaceC_EngineBasedClient(clusterManagementURL, engineID, identifier);
            if ("success".equals(_client.register())
                    || "success".equals(_client.connect())){
                _client.heartbeat();
            }
            System.out.println(_client.register());
            System.out.println(_client.connect());
            System.out.println(_client.disconnect());
            System.out.println(_client.unregister());

        } catch (YPersistenceException e) {
            _log.fatal("Failure to initialise runtime (persistence failure)", e);
            throw new UnavailableException("Persistence failure");
        } catch (IOException e) {
            e.printStackTrace();
            _log.error("cluster management lost");
        }

        if (_engine != null) {
            _engine.notifyServletInitialisationComplete(maxWaitSeconds);
        } else {
            _log.fatal("Failed to initialise Engine (unspecified failure). Please " +
                    "consult the logs for details");
            throw new UnavailableException("Unspecified engine failure");
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);                 // redirect all GETs to POSTs
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        OutputStreamWriter outputWriter = ServletUtils.prepareResponse(response);
        StringBuilder output = new StringBuilder();
        output.append("<response>");
        output.append(processPostQuery(request));
        output.append("</response>");
        if (_engine.enginePersistenceFailure()) {
            _log.fatal("************************************************************");
            _log.fatal("A failure has occurred whilst persisting workflow state to the");
            _log.fatal("database. Check the status of the database connection defined");
            _log.fatal("for the YAWL service, and restart the YAWL web application.");
            _log.fatal("Further information may be found within the Tomcat log files.");
            _log.fatal("************************************************************");
            response.sendError(500, "Database persistence failure detected");
        }
        outputWriter.write(output.toString());
        outputWriter.flush();
        outputWriter.close();
        //todo find out how to provide a meaningful 500 message in the format of  a fault message.
    }

    private String processPostQuery(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        String sessionHandle = request.getParameter("sessionHandle");
        String action = request.getParameter("action");

        try {
            if (action != null) {
                if (action.equalsIgnoreCase("restore")) {
                    Date date = new Date();
                    _engine.restore(sessionHandle);
                    msg.append((new Date()).getTime() - date.getTime());
                    _log.info("Remote called restore act");
                }
                else if(action.equalsIgnoreCase("heartbeat")) {
                    msg.append("normal");
                    _log.info("Normal heartbeat at " + new Date().toString());
                }
            }

        } catch (YPersistenceException e) {
            e.printStackTrace();
        }
        return msg.toString();
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