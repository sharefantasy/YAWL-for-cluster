package org.yawlfoundation.plugin;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.yawlfoundation.yawl.engine.interfce.ServletUtils;
import org.yawlfoundation.yawl.engine.interfce.YHttpServlet;

/**
 * Created by fantasy on 2016-12-27.
 */
public class MetricsInterface extends YHttpServlet {
	private MetricsCollector collector = MetricsCollector.getInstance();
	private static final Logger logger = Logger.getLogger(MetricsInterface.class);
	public void init() {
		ServletContext context = getServletContext();
		String[] settings = context.getInitParameter("InfluxSetting").split(";");
		collector.switchDB(settings[0], settings[1], settings[2], settings[3]);
		logger.info("metrics collector started");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OutputStreamWriter outputWriter = ServletUtils.prepareResponse(response);
		StringBuilder output = new StringBuilder();
		output.append("<response>");
		output.append(processPostQuery(request));
		output.append("</response>");
		outputWriter.write(output.toString());
		outputWriter.flush();
		outputWriter.close();
	}

	private String processPostQuery(HttpServletRequest request) {
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("configAddress")) {
			String address = request.getParameter("address");
			String user = request.getParameter("user");
			String password = request.getParameter("password");
			String db = request.getParameter("db");
			if (address == null || user == null || password == null || db == null)
				return "failed";
			collector.switchDB(address, user, password, db);
			_log.info(String.format("New influx db address: %s@%s", db, address));
		}
		return "success";
	}
	public void finalize() throws Throwable {
		collector.destroy();
		super.finalize();
	}
}
