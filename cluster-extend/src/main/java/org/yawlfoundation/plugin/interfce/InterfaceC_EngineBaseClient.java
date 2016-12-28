package org.yawlfoundation.plugin.interfce;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.jdom2.Document;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.ObserverGateway;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.YWorkItem;
import org.yawlfoundation.yawl.engine.YWorkItemStatus;
import org.yawlfoundation.yawl.engine.announcement.YAnnouncement;
import org.yawlfoundation.yawl.engine.announcement.YEngineEvent;
import org.yawlfoundation.yawl.engine.interfce.Interface_Client;

/**
 * Created by fantasy on 2016/7/14.
 */

public class InterfaceC_EngineBaseClient extends Interface_Client implements ObserverGateway {
	private static final Logger _logger = Logger.getLogger(InterfaceC_EngineBaseClient.class);
	private volatile int caseCounter = 0;
	private volatile int workItemCounter = 0;
	private ScheduledExecutorService executorService;
	private String cluster_service_url;
	private boolean toSend = false;

	InterfaceC_EngineBaseClient(String url) {
		InfluxDB dataLogger = InfluxDBFactory.connect("http://192.168.253.128:8086", "root", "");
		dataLogger.createDatabase("yawl_snapshot");
		cluster_service_url = url;
		System.out.println("here");
		executorService = Executors.newScheduledThreadPool(3);
		Runnable sender = () -> {
			try {
				_logger.info("case: " + caseCounter + " workitem: " + workItemCounter);
				System.out.println("case: " + caseCounter + " workitem: " + workItemCounter);
				BatchPoints batchPoints = BatchPoints.database("yawl_snapshot").tag("async", "true")
						.retentionPolicy("default").consistency(InfluxDB.ConsistencyLevel.ALL).build();
				Point workitemPt = Point.measurement("workitem").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("number", workItemCounter).addField("interval", 5)
						.addField("speed", workItemCounter / 5).build();
				Point casePt = Point.measurement("case").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("number", caseCounter).addField("interval", 5).addField("speed", caseCounter / 5)
						.build();
				batchPoints.point(workitemPt);
				batchPoints.point(casePt);
				dataLogger.write(batchPoints);
				flushCounter();
			} catch (Exception e) {
				_logger.error(e.getMessage());
				e.printStackTrace();
			}

		};
		executorService.scheduleAtFixedRate(sender, 0, 1, TimeUnit.SECONDS);
	}

	public String getRole() throws IOException {
		Map<String, String> params = prepareParamMap("getRole", null);
		return executePost(cluster_service_url, params);
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
	public void announceCaseCompletion(Set<YAWLServiceReference> services, YIdentifier caseID, Document caseData) {

	}

	@Override
	public void shutdown() {
		if (!toSend)
			return;
		Map<String, String> params = prepareParamMap("announceEngineShutdown", null);
		try {
			executePost(cluster_service_url, params);
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
