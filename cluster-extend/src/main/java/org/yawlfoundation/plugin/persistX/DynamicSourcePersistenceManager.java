package org.yawlfoundation.plugin.persistX;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.*;
import org.yawlfoundation.yawl.engine.time.YLaunchDelayer;
import org.yawlfoundation.yawl.engine.time.YWorkItemTimer;
import org.yawlfoundation.yawl.exceptions.Problem;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;
import org.yawlfoundation.yawl.logging.table.*;

/**
 * Created by fantasy on 2016/7/16.
 */
@Component
public class DynamicSourcePersistenceManager extends YPersistenceManager {
	private static Class[] persistedClasses = {YSpecification.class, YNetRunner.class, YWorkItem.class,
			YIdentifier.class, YNetData.class, YAWLServiceReference.class, YExternalClient.class, YWorkItemTimer.class,
			YLaunchDelayer.class, YCaseNbrStore.class, Problem.class, YLogSpecification.class, YLogNet.class,
			YLogTask.class, YLogNetInstance.class, YLogTaskInstance.class, YLogEvent.class, YLogDataItemInstance.class,
			YLogDataType.class, YLogService.class, YAuditEvent.class};
	protected String engineRole = null;
	protected Configuration configuration;
	private static final Logger _logger = Logger.getLogger(DynamicSourcePersistenceManager.class);
	protected static DynamicSourcePersistenceManager instance;

	public static DynamicSourcePersistenceManager getInstance() {
		if (instance == null) {
			instance = new DynamicSourcePersistenceManager();
		}
		return instance;
	}
	protected SessionFactory initialise(boolean journalising) throws YPersistenceException {
		if (journalising) {
			configuration = new Configuration().configure();
			for (Class c : persistedClasses) {
				configuration.addClass(c);
			}
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
					.buildServiceRegistry();
			factory = configuration.buildSessionFactory(serviceRegistry);
		}
		return factory;
	}
	protected SessionFactory initialise(boolean journalising, String role) throws YPersistenceException {
		if (journalising) {
			configuration = new Configuration().configure();
			for (Class c : persistedClasses) {
				configuration.addClass(c);
			}
			String url = configuration.getProperty("hibernate.connection.url");
			if (url == null) {
				throw new YPersistenceException("data base is not set");
			}
			int demit = url.lastIndexOf('/');
			if (demit == -1) {
				throw new YPersistenceException("no database specified");
			}
			int demit2 = url.indexOf('?');
			StringBuilder sb = new StringBuilder();
			sb.append(url.substring(0, demit + 1));
			sb.append(role);
			if (demit2 != -1) {
				sb.append(url.substring(demit2));
			}
			configuration.setProperty("hibernate.connection.url", sb.toString());

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
					.buildServiceRegistry();
			factory = configuration.buildSessionFactory(serviceRegistry);
			setEnabled(true);
		}
		return factory;
	}

	public void setFollow() {
		try {
			if (factory != null) {
				initialise(false);
				setEnabled(false);
			}
			engineRole = null;
		} catch (YPersistenceException e) {
			e.printStackTrace();
		}

	}

	public String getEngineRole() {
		return engineRole;
	}

	public boolean setMaster(String role) {
		try {
			if (initialise(true, role) != null) {
				reloadStorage();
				engineRole = role;
				return true;
			}
			return false;
		} catch (YPersistenceException e) {
			_logger.error("switch role failed");
			return false;
		}
	}

	private void reloadStorage() throws YPersistenceException {
		YEngineClusterExtent engine = (YEngineClusterExtent) YEngineClusterExtent.getInstance();
		engine.restore();
	}
}
