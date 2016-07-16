package org.yawlfoundation.plugin.interfce;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.*;
import org.yawlfoundation.yawl.engine.announcement.YAnnouncement;
import org.yawlfoundation.yawl.engine.announcement.YEngineEvent;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.util.HttpURLValidator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fantasy on 2016/7/14.
 */
public class InterfaceC_EngineBaseClient extends Interface_Client implements ObserverGateway {
	public static final Logger _logger = Logger.getLogger(InterfaceC_EngineBaseClient.class);
	private volatile int caseCounter = 0;
	private volatile int workItemCounter = 0;
	private ScheduledExecutorService executorService;
	private String url;
	private static InterfaceC_EngineBaseClient instance;
	private boolean toSend = false;
	public static InterfaceC_EngineBaseClient getInstance() {
		if (instance == null) {
			instance = new InterfaceC_EngineBaseClient();
		}
		return instance;
	}
	private InterfaceC_EngineBaseClient() {
		Properties properties = new Properties();
		try {
			File file = release();
			if (file == null) {
				file = debug();
			}
			properties.load(new BufferedInputStream(new FileInputStream(file)));
			url = properties.getProperty("cluster.url");
			toSend = HttpURLValidator.pingUntilAvailable(url, 10);
			YPersistenceManager a = new YPersistenceManager();

			YEngine e = YEngine.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (toSend) {
			executorService = Executors.newScheduledThreadPool(1);
			Runnable sender = new Runnable() {
				@Override
				public void run() {
					Map<String, String> params = prepareParamMap("CaseSnapshot", null);
					params.put("snapshot", getSnapshotJSON());
					flushCounter();
					try {
						executePost(url, params);
					} catch (IOException e) {
						_logger.error("cluster server lost connection");
						executorService.shutdown();
					}
				}
			};
			executorService.scheduleAtFixedRate(sender, 0, 5, TimeUnit.SECONDS);
		} else {
			_logger.error("cluster server is not available");
		}
	}

	private void flushCounter() {
		caseCounter = 0;
		workItemCounter = 0;
	}

	private String getSnapshotJSON() {
		return String.format("{\"timestamp\": %d,\"caseCount\": %d,\"workItemCount\": %d}", new Date().getTime(),
				caseCounter, workItemCounter);
	}

	@Override
	public String getScheme() {
		return "http";
	}
	@Override
	public void announceFiredWorkItem(YAnnouncement announcement) {
		if (!toSend)
			return;
		if (announcement.getEvent().equals(YEngineEvent.ITEM_ADD)) {
			workItemCounter++;
		}

	}
	@Override
	public void announceCaseStarted(Set<YAWLServiceReference> services, YSpecificationID specID, YIdentifier caseID,
			String launchingService, boolean delayed) {
		if (!toSend)
			return;
		caseCounter++;
	}
	@Override
	public void shutdown() {
		if (!toSend)
			return;
		Map<String, String> params = prepareParamMap("announceEngineShutdown", null);
		try {
			executePost(url, params);
		} catch (IOException e) {
			_logger.error("cluster service lost connection");
		} finally {
			executorService.shutdown();
			if (executorService.isTerminated()) {
				executorService.shutdownNow();
			}
		}
	}

	private File debug() throws IllegalArgumentException {
		return new File("cluster.properties");
	}

	private File release() {
		URL path = Thread.currentThread().getContextClassLoader().getResource("cluster.properties");
		return new File(path.getFile());

	}
	@Override
	public void announceCancelledWorkItem(YAnnouncement announcement) {

	}

	@Override
	public void announceTimerExpiry(YAnnouncement announcement) {

	}

	@Override
	public void announceCaseCompletion(YAWLServiceReference yawlService, YIdentifier caseID, Document caseData) {

	}

	@Override
	public void announceCaseCompletion(Set<YAWLServiceReference> services, YIdentifier caseID, Document caseData) {

	}

	@Override
	public void announceCaseSuspended(Set<YAWLServiceReference> services, YIdentifier caseID) {

	}

	@Override
	public void announceCaseSuspending(Set<YAWLServiceReference> services, YIdentifier caseID) {

	}

	@Override
	public void announceCaseResumption(Set<YAWLServiceReference> services, YIdentifier caseID) {

	}

	@Override
	public void announceWorkItemStatusChange(Set<YAWLServiceReference> services, YWorkItem workItem,
			YWorkItemStatus oldStatus, YWorkItemStatus newStatus) {

	}

	@Override
	public void announceEngineInitialised(Set<YAWLServiceReference> services, int maxWaitSeconds) {

	}

	@Override
	public void announceCaseCancellation(Set<YAWLServiceReference> services, YIdentifier id) {

	}

	@Override
	public void announceDeadlock(Set<YAWLServiceReference> services, YIdentifier id, Set<YTask> tasks) {

	}

}
