package cluster.util.iaasClient;

import cluster.general.entity.Engine;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.util.exceptions.MigrationException;
import cluster.workflowService.schedule.SchedulerStatus;
import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.actions.LiveMigrateOptions;
import org.openstack4j.openstack.OSFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fantasy on 2016/1/4.
 */
// @Service("openstackAdapter")
public class OSAdapter extends BaseAdapter {
	private static final Logger _logger = Logger.getLogger(OSAdapter.class);
	private ArrayList<envObserver> obs = new ArrayList<>();
	private SchedulerStatus status;
	private OSClient os;
	private static OSAdapter instance;

	public OSAdapter() {
		String OS_AUTH_URL = "http://192.168.0.15:5000/v2.0/";
		String OS_AUTH_NAME = "admin";
		String OS_AUTH_PASSWORD = "password";
		String OS_AUTH_PROJECT = "demo";
		try {

			os = OSFactory.builderV2().endpoint(OS_AUTH_URL).credentials(OS_AUTH_NAME, OS_AUTH_PASSWORD)
					.tenantName(OS_AUTH_PROJECT).authenticate();
		} catch (ConnectionException e) {
			_logger.error(e.getMessage());
		}

	}
	@Override
	public void Migrate(EngineRole engine, Host dest) throws MigrationException {
		// TODO: 2016/1/11 VM migration needs further configurations. Check OS
		// confuguration files to implement this method
		os.compute().migrations().list().get(0).getDestNode();
		os.compute().servers().liveMigrate(engine.getHost().getName(),
				LiveMigrateOptions.create().blockMigration(false).host(dest.getName()));
	}

	public Engine HostUsage(String hostID) {
		return null;// TODO: 2016/1/10 collect IaaS level information.requires
					// ceilometer
	}

	// pre:hosts are are already sync with platform, not null
	public List<Engine> bindEngineAndHost(List<Engine> engines, List<Host> hosts) {
		engines = updateEngines(engines);
		HashMap<String, String> hostmap = new HashMap<>();
		List<Server> servers = (List<Server>) os.compute().servers().list();
		servers.forEach(server -> hostmap.put(server.getId(), server.getHostId()));
		engines.stream().filter(e -> e.getEngineRole() != null)
				.forEach(e -> hosts.stream()
						.filter(h -> h.getName().equals(hostmap.get(e.getEngineRole().getContainerName())))
						.forEach(h -> e.getEngineRole().setHost(h)));
		return engines;
	}

	@SuppressWarnings("unchecked")
	private List<Engine> updateEngines(List<Engine> engines) {
		List<Server> servers = ((List<Server>) os.compute().servers().list());
		servers.stream()
				.forEach(s -> engines.stream()
						.filter(e -> e.getIp().equals(s.getAccessIPv4()) && (e.getEngineRole() != null))
						.forEach(e -> e.getEngineRole().setContainerName(s.getId())));
		return engines;
	}

	public List<Host> getHosts() {
		List<String> oshost = os.compute().hostAggregates().get("1").getHosts();

		List<Host> hosts = new ArrayList<>();
		for (String h : oshost) {
			Host host = new Host();
			host.setName(h);
			hosts.add(host);
		}
		return hosts;
	}

	@Override
	public boolean isStarted() {
		try {
			os.supportsCompute();
			return true;
		} catch (ConnectionException e) {
			return false;
		}

	}
}
