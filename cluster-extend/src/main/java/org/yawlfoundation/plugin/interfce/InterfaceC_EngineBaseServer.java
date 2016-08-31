package org.yawlfoundation.plugin.interfce;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;
import org.yawlfoundation.plugin.HA.HAService;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.YEngineClusterExtent;
import org.yawlfoundation.yawl.engine.interfce.EngineGatewayClusterExtent;
import org.yawlfoundation.yawl.engine.interfce.EngineGatewayClusterExtentImpl;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.YHttpServlet;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;

/**
 * Created by fantasy on 2016/7/16.
 */

public class InterfaceC_EngineBaseServer extends YHttpServlet {
	private EngineGatewayClusterExtent _engineGW;
	private AutowireCapableBeanFactory factory;
	private HAService haService;

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		String enableCluster = context.getInitParameter("EnableCluster");
		if (enableCluster == null || enableCluster.equalsIgnoreCase("false")) {
			this.destroy();
			return;
		}
		_engineGW = (EngineGatewayClusterExtent) context.getAttribute("engine");
		if (_engineGW != null) {
			if (!(YEngine.getInstance() instanceof YEngineClusterExtent)
					|| !(_engineGW instanceof EngineGatewayClusterExtent)) {
				_log.error(YEngine.getInstance() + " is not a cluster engine, cannot provide cluster service");
				this.destroy();
			}
		} else {
			try {
				String enableHbnStatsStr = context.getInitParameter("EnableHibernateStatisticsGathering");
				boolean enableHbnStats = ((enableHbnStatsStr != null) && enableHbnStatsStr.equalsIgnoreCase("true"));
				String engineClass = context.getInitParameter("defaultEngineImplClass");
				if (engineClass != null) {
					System.setProperty("defaultEngineImplClass", engineClass);
				}

				_engineGW = new EngineGatewayClusterExtentImpl(false, enableHbnStats);
				// connect with monitor service
				loadSpring();
				haService = (HAService) factory.getBean("HAService");

                haService.follow();
                String collectorAddress = context.getInitParameter("collector");
                InterfaceC_EngineBaseClient client = new InterfaceC_EngineBaseClient(collectorAddress);
                _engineGW.registerObserverGateway(client);
				_engineGW.setActualFilePath(context.getRealPath("/"));
				context.setAttribute("engine", _engineGW);
			} catch (YPersistenceException e) {
				_log.fatal("Failure to initialise runtime (persistence failure)", e);
				throw new UnavailableException("Persistence failure");
			} catch (MalformedURLException e) {
				_log.error("cluster definition file path is wrong");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	private void loadSpring() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/WEB-INF/applicationContext.xml");
		factory = context.getAutowireCapableBeanFactory();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OutputStreamWriter outputWriter = ServletUtils.prepareResponse(response);
		StringBuilder output = new StringBuilder();
		output.append("<response>");
		output.append(processQuery(request));
		output.append("</response>");
		if (_engineGW.enginePersistenceFailure()) {
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
		// todo find out how to provide a meaningful 500 message in the format
		// of a fault message.
	}
	private String processQuery(HttpServletRequest request) {
		String action = request.getParameter("action");
		String session = request.getParameter("sessionHandle");
		String result = "success";
        StopWatch stopWatch = new StopWatch();
        switch (action) {
			case "shutdown" :
				_engineGW.shutdown();
				break;
			case "restore" :
				_engineGW.restore(session);
				break;
			case "migrate" :
                stopWatch.start();
                String role = request.getParameter("role");
				result = (role != null) ? haService.coupDetat(role) : "new role is null";
                stopWatch.stop();
                _log.info("migrate time: " + stopWatch.getLastTaskTimeMillis());
                break;
			case "exile" :
                stopWatch.start();
                result = haService.exile();
                stopWatch.stop();
                _log.info("exile time: " + stopWatch.getLastTaskTimeMillis());
                break;
		}
		return result;
	}
	public void destroy() {
		_engineGW.shutdown();
	}
}
