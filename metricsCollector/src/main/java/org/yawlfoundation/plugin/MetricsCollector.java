package org.yawlfoundation.plugin;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;

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

/**
 * Created by fantasy on 2016/9/7.
 */
public class MetricsCollector extends HttpServlet implements ObserverGateway {

	private volatile long workItemCounter = 0;
	private volatile long caseCounter = 0;
	private Handler handler;
	private ScheduledExecutorService executorService;
	public static final Logger logger = Logger.getLogger(MetricsCollector.class);
	private static MetricsCollector instance;
	public static MetricsCollector getInstance() {
		if (instance == null) {
			instance = new MetricsCollector();
		}
		return instance;
	}
	protected MetricsCollector() {
	}
	void switchDB(String address, String user, String password, String dbString) {
		InfluxDB db = InfluxDBFactory.connect(address, user, password);
		if (this.executorService != null) {
			this.executorService.shutdownNow();
		}

		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		handler = new Handler(db, this, dbString);
		executorService.scheduleAtFixedRate(handler, 0, 5, TimeUnit.SECONDS);
		this.executorService = executorService;
	}

	public void announceFiredWorkItem(YAnnouncement announcement) {
		if (announcement.getEvent().equals(YEngineEvent.ITEM_ADD)) {
			workItemCounter++;
		}
	}

	public void announceCaseStarted(Set<YAWLServiceReference> services, YSpecificationID specID, YIdentifier caseID,
			String launchingService, boolean delayed) {
		caseCounter++;
	}
	void flushCounter() {
		caseCounter = 0;
		workItemCounter = 0;
	}
	public long getWorkItemCounter() {
		return workItemCounter;
	}

	public long getCaseCounter() {
		return caseCounter;
	}

	public String getScheme() {
		return "http";
	}

	public void announceCancelledWorkItem(YAnnouncement yAnnouncement) {

	}

	public void announceTimerExpiry(YAnnouncement yAnnouncement) {

	}

	public void announceCaseCompletion(YAWLServiceReference yawlServiceReference, YIdentifier yIdentifier,
			Document document) {

	}

	public void announceCaseCompletion(Set<YAWLServiceReference> set, YIdentifier yIdentifier, Document document) {

	}

	public void announceCaseSuspended(Set<YAWLServiceReference> set, YIdentifier yIdentifier) {

	}

	public void announceCaseSuspending(Set<YAWLServiceReference> set, YIdentifier yIdentifier) {

	}

	public void announceCaseResumption(Set<YAWLServiceReference> set, YIdentifier yIdentifier) {

	}

	public void announceWorkItemStatusChange(Set<YAWLServiceReference> set, YWorkItem yWorkItem,
			YWorkItemStatus yWorkItemStatus, YWorkItemStatus yWorkItemStatus1) {

	}

	public void announceEngineInitialised(Set<YAWLServiceReference> set, int i) {

	}

	public void announceCaseCancellation(Set<YAWLServiceReference> set, YIdentifier yIdentifier) {

	}

	public void announceDeadlock(Set<YAWLServiceReference> set, YIdentifier yIdentifier, Set<YTask> set1) {

	}

	public void shutdown() {

	}

	public void destroy() {
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}
}

class Handler implements Runnable {
	public void setBackend(InfluxDB backend) {
		this.backend = backend;
	}

	private InfluxDB backend;
	private MetricsCollector collector;
	private String db;
	public Handler(InfluxDB backend, MetricsCollector collector, String dbString) {
		this.backend = backend;
		this.collector = collector;
		this.db = dbString;
	}

	public void run() {
		long workItemCounter = collector.getWorkItemCounter();
		long caseCounter = collector.getCaseCounter();
		BatchPoints batchPoints = BatchPoints.database(db).tag("async", "true").retentionPolicy("default")
				.consistency(InfluxDB.ConsistencyLevel.ALL).build();
		Point workitemPt = Point.measurement("workitem").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("number", workItemCounter).addField("interval", 5).addField("speed", workItemCounter / 5)
				.build();
		Point casePt = Point.measurement("case").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("number", caseCounter).addField("interval", 5).addField("speed", caseCounter / 5).build();
		batchPoints.point(workitemPt);
		batchPoints.point(casePt);
		backend.write(batchPoints);
		MetricsCollector.logger.info(String.format("case: %d workitem: %d ", caseCounter, workItemCounter));
		collector.flushCounter();
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}
}