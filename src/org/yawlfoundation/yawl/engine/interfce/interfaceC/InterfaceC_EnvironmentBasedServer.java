package org.yawlfoundation.yawl.engine.interfce.interfaceC;

import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * Created by fantasy on 2015/8/5.
 */
public class InterfaceC_EnvironmentBasedServer extends HttpServlet {

    private Logger _logger = Logger.getLogger(InterfaceC_EnvironmentBasedServer.class);
    private boolean _debug;
    protected InterfaceC_Controller controller;
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        ServletContext context = getServletContext();

        String logonName = context.getInitParameter("EngineLogonUserName");
        String logonPassword = context.getInitParameter("EngineLogonPassword");

    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStreamWriter writer = ServletUtils.prepareResponse(response);
        StringBuilder output = new StringBuilder();
        output.append("<response>");
        output.append(processQuery(request));
        output.append("</response>");
        ServletUtils.finalizeResponse(writer, output);

    }

    private String processQuery(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        String sessionHandle = request.getParameter("sessionHandle");
        String action = request.getParameter("action");
        String engineID = request.getParameter("engineID");
        String password = request.getParameter("password");

        System.out.println(action);
        try {
            if (_debug) {
                debug(request, "Post");
            }

            if (action != null){
                if ("connect".equals(action)){
                    String url = request.getParameter("url");
                    String session = request.getParameter("sessionHandle");
                    msg.append(controller.connect(engineID, password, url, session));
                }
                else if ("disconnect".equals(action)){
                    msg.append(controller.disconnect(engineID, password));
                }
                else if ("register".equals(action)){
                    msg.append(controller.register(engineID, password));
                }
                else if ("unregister".equals(action)){
                    msg.append(controller.unregister(engineID, password));
                }
                else if ("heartbeat".equals(action)){
                    msg.append(controller.heartbeat(engineID, password));
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        finally {
            return msg.toString();
        }

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

}
