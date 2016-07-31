package cluster.workflowService;

import cluster.general.entity.*;
import cluster.general.service.HostService;
import cluster.general.service.TenantService;
import cluster.util.exceptions.MigrationException;
import cluster.util.iaasClient.Adapter;
import cluster.workflowService.schedule.SimpleConfigurationAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ServiceProvider implements DisposableBean {
	private static final Logger _logger = Logger.getLogger(ServiceProvider.class);
	public void setStatisticInterval(int statisticInterval) {
		this.statisticInterval = statisticInterval;
	}

	private int statisticInterval = 20 * 60; // in seconds
	private List<Host> hostList;
	private List<Tenant> tenantList;
	private List<EngineRole> engineRoleList;

	@Autowired
	private Adapter adapter;
	@Autowired
	private HostService hostService;
	@Autowired
	private TenantService tenantService;

	private ScheduledExecutorService _executor = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	private final Timer sloMonitor = new Timer();
	private final Timer speedMonitor = new Timer();

	@Autowired
	private SimpleConfigurationAdapter configAdapter;

	public ServiceProvider() {
	}

	public ServiceProvider(List<Host> hosts, List<Tenant> tenants, List<EngineRole> engines) {
		this.hostList = hosts;
		this.tenantList = tenants;
		this.engineRoleList = engines;
	}

	public void startService() {
		if (!adapter.isStarted()) {
			_logger.error("engines not ready");
			return;
		}
		configAdapter.loadonfig(tenantList, hostList);
		_executor.scheduleWithFixedDelay(() -> {
			hostList.forEach(h -> hostService.updateSpeed(h));
			tenantList.forEach(t -> tenantService.updateSpeed(t));
		}, 0, 5, TimeUnit.SECONDS);
		_executor.scheduleWithFixedDelay(() -> {
			HashMap<EngineRole, Host> ins = configAdapter.genConfig();
			ins.forEach((EngineRole e, Host h) -> {
				try {
					adapter.Migrate(e, h);
				} catch (MigrationException e1) {
					e1.printStackTrace();
				}
			});
		}, 0, statisticInterval, TimeUnit.SECONDS);
		_logger.info(String.format("start monitoring at %s...", (new Date()).toString()));
	}

	private void loadDataCenterSnapshot() {
	}

	public void shutdownService() {
		_logger.info("Stop Monitoring...");
	}

	public List<Tenant> getTenants() {
		return tenantList;
	}

	public List<Host> getHosts() {
		return hostList;
	}

	public void loadHost() {
		hostList = adapter.getHosts();
	}

	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void destroy() throws Exception {
		shutdownService();
		sloMonitor.cancel();
		speedMonitor.cancel();
	}
}
