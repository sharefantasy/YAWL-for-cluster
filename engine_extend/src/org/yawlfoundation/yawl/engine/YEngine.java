package org.yawlfoundation.yawl.engine;

import org.jdom2.Document;
import org.jdom2.Element;
import org.yawlfoundation.yawl.authentication.YClient;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.authentication.YSessionCache;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.instance.InstanceCache;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceADesign;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceAManagement;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBInterop;
import org.yawlfoundation.yawl.exceptions.*;
import org.yawlfoundation.yawl.logging.YLogDataItemList;
import org.yawlfoundation.yawl.logging.table.YAuditEvent;
import org.yawlfoundation.yawl.util.YBuildProperties;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fantasy on 2016/7/16.
 */

public abstract class YEngine implements InterfaceADesign, InterfaceAManagement, InterfaceBClient, InterfaceBInterop {

	private static Class<?> defaultEngineImplClass;
	static {
		// String defaultEngineImpl =
		// ;
		String defaultEngineImpl = System.getProperty("defaultEngineImplClass");
		if (!(defaultEngineImpl == null)) {
			if (!defaultEngineImpl.equals("")) {
				try {
					defaultEngineImplClass = Class.forName(defaultEngineImpl);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else {
			defaultEngineImplClass = YEngineImpl.class;
		}

	}
	// STATIC MEMBERS //
	// Engine statuses
	public enum Status {
		Dormant, Initialising, Running, Terminating
	}
	// Workitem completion types
	public enum WorkItemCompletion {
		Normal, Force, Fail
	}

	private static YEngine _thisInstance = null;
	private static boolean _persisting = false;
	private static YPersistenceManager _pmgr = null;
	public static boolean isPersisting() {
		return _persisting;
	}
	private static void setPersisting(boolean persist) {
		_persisting = persist;
	}
	public static YPersistenceManager getPersistenceManager() {
		if (_pmgr == null) {
			try {
				_pmgr = (YPersistenceManager) _thisInstance.getClass().getMethod("getPersistenceManager").invoke(null);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				_pmgr = new YPersistenceManager();
			}
		}
		return _pmgr;
	}

	protected YEngine() {
		if (_thisInstance == null) {
			_thisInstance = this;
		}
	}
	public static YEngine getInstance() {
		if (_thisInstance == null) {
			try {
				return (YEngine) defaultEngineImplClass.getMethod("getInstance").invoke(null);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return _thisInstance;
	}
	public static YEngine getInstance(boolean persisting) throws YPersistenceException {
		if (_thisInstance == null) {
			_persisting = persisting;
			try {
				return (YEngine) defaultEngineImplClass.getMethod("getInstance", boolean.class).invoke(null,
						persisting);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return _thisInstance;
	}
	public static YEngine getInstance(boolean persisting, boolean gatherHbnStats) throws YPersistenceException {
		if (_thisInstance == null) {
			YEngine._persisting = persisting;
			try {
				return (YEngine) defaultEngineImplClass.getMethod("getInstance", boolean.class, boolean.class)
						.invoke(null, persisting, gatherHbnStats);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return _thisInstance;
	}
	public static boolean isRunning() {
		return (_thisInstance != null) && (_thisInstance.getEngineStatus() == Status.Running);
	}
	protected Set<YClient> loadDefaultClients() throws YPersistenceException {
		YDefClientsLoader loader = new YDefClientsLoader();
		for (YExternalClient client : loader.getLoadedClients()) {
			addExternalClient(client);
		}
		for (YAWLServiceReference service : loader.getLoadedServices()) {
			addYawlService(service);
		}
		return loader.getAllLoaded();
	}
	// private abstract restore();
	public abstract void setGenerateUIMetaData(boolean generate);
	public abstract boolean generateUIMetaData();
	public abstract String getYawlVersion();
	public abstract InstanceCache getInstanceCache();
	public abstract Map<String, YParameter> getParameters(YSpecificationID specID, String taskID, boolean input);
	public abstract String getEngineClassesRootFilePath();
	public abstract void setEngineClassesRootFilePath(String path);
	public abstract void initialised(int maxWaitSeconds);
	public abstract void shutdown();
	public abstract void initBuildProperties(InputStream stream);
	public abstract YBuildProperties getBuildProperties();
	public abstract YSessionCache getSessionCache();
	public abstract void checkEngineRunning() throws YEngineStateException;
	public abstract void addRunner(YNetRunner runner, YSpecification specification);
	public abstract YNetRunner getNetRunner(YWorkItem workItem);
	public abstract YNetRunner getNetRunner(YIdentifier identifier);
	public abstract YNetRunnerRepository getNetRunnerRepository();
	public abstract String getNetData(String caseID) throws YStateException;
	public abstract String getSpecificationDataSchema(YSpecificationID specID);
	public abstract Map<YSpecificationID, List<YIdentifier>> getRunningCaseMap();
	public abstract void cancelCase(YIdentifier caseID, String serviceHandle)
			throws YPersistenceException, YEngineStateException;
	public abstract String launchCase(YSpecificationID specID, String caseParams, URI completionObserver,
			YLogDataItemList logData, String serviceHandle)
			throws YStateException, YDataStateException, YPersistenceException, YEngineStateException, YQueryException;
	public abstract List<YIdentifier> getRunningCaseIDs();
	public abstract String getNextCaseNbr();
	public abstract YNetData getCaseData(YIdentifier id);
	public abstract boolean updateCaseData(String idStr, String data) throws YPersistenceException;
	public abstract Document getCaseDataDocument(String id);
	public abstract YWorkItemRepository getWorkItemRepository();
	public abstract Element getStartingDataSnapshot(String itemID)
			throws YStateException, YEngineStateException, YDataStateException, YQueryException;
	public abstract YWorkItem skipWorkItem(YWorkItem workItem, YClient client)
			throws YStateException, YDataStateException, YQueryException, YPersistenceException, YEngineStateException;
	public abstract boolean canAddNewInstances(String workItemID);
	public abstract YWorkItem unsuspendWorkItem(String workItemID) throws YStateException, YPersistenceException;
	public abstract boolean updateWorkItemData(String workItemID, String data);
	public abstract void cancelWorkItem(YWorkItem workItem);
	public abstract Set<YAWLServiceReference> getYAWLServices();
	public abstract boolean updateExternalClient(String id, String password, String doco) throws YPersistenceException;
	public abstract Set<YExternalClient> getExternalClients();
	public abstract YExternalClient removeExternalClient(String clientName) throws YPersistenceException;
	public abstract void setDefaultWorklist(String paramStr);
	public abstract YAWLServiceReference getDefaultWorklist();
	public abstract void setAllowAdminID(boolean allow);
	public abstract boolean isGenericAdminAllowed();
	public abstract void updateObject(Object obj) throws YPersistenceException;
	public abstract void deleteObject(Object obj) throws YPersistenceException;
	public abstract void writeAudit(YAuditEvent event);
	public abstract boolean addInterfaceXListener(String observerURI);
	public abstract boolean removeInterfaceXListener(String uri);
	public abstract void setHibernateStatisticsEnabled(boolean enabled);
	public abstract boolean isHibernateStatisticsEnabled();
	public abstract String getHibernateStatistics();
	public abstract void disableProcessLogging();
	public abstract YWorkItem startWorkItem(String itemID, YClient client)
			throws YStateException, YDataStateException, YQueryException, YPersistenceException, YEngineStateException;

	// exception decorator changed;
	public abstract YIdentifier getCaseID(String caseIDStr);
	public abstract String getStateForCase(YIdentifier caseID);
	public abstract Set<YSpecificationID> getLoadedSpecificationIDs();

	// protected interfaces.
	protected abstract void removeCaseFromCaches(YIdentifier caseID);
	protected abstract void clearCaseFromPersistence(YIdentifier id) throws YPersistenceException;
	protected abstract void addRunner(YNetRunner runner);
}
