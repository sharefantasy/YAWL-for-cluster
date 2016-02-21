package cluster.util.iaasClient;

import cluster.workflowService.schedule.SchedulerStatus;
import cluster.general.entity.Engine;
import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.util.exceptions.MigrationException;
import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.actions.LiveMigrateOptions;
import org.openstack4j.openstack.OSFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/1/4.
 */
//@Service("openstackAdapter")
public class OSAdapter extends BaseAdapter{
    private static final Logger _logger = Logger.getLogger(OSAdapter.class);
    private ArrayList<envObserver> obs = new ArrayList<>();
    private SchedulerStatus status;
    private OSClient os;
    private static OSAdapter instance;

    public OSAdapter() {
        String OS_AUTH_URL = "http://192.168.0.4:5000/v2.0/";
        String OS_AUTH_NAME = "admin";
        String OS_AUTH_PASSWORD = "password";
        String OS_AUTH_PROJECT = "demo";
        try {
            os = OSFactory.builder()
                    .endpoint(OS_AUTH_URL)
                    .credentials(OS_AUTH_NAME, OS_AUTH_PASSWORD)
                    .tenantName(OS_AUTH_PROJECT)
                    .authenticate();
        } catch (ConnectionException e) {
            _logger.error(e.getMessage());
        }

    }
    @Override
    public void Migrate(EngineRole engine, Host dest) throws MigrationException {
        // TODO: 2016/1/11 VM migration needs further configurations. Check OS confuguration files to implement this method

        os.compute().migrations().list().get(0).getDestNode();
        os.compute().servers()
                .liveMigrate(engine.getHost().getName(),
                        LiveMigrateOptions
                                .create()
                                .blockMigration(false)
                                .host(dest.getName()));

    }

    public Engine HostUsage(String hostID) {

        return null;// TODO: 2016/1/10 collect IaaS level information
    }

    public Map<Host, EngineRole> loadEngineHostMap() {

        // TODO: 2016/1/11 update host map in period, but may better in other way
        return null;
    }

    public List<Host> getHosts() {
        List<String> oshost = os.compute().hostAggregates().get("server").getHosts();
        List<Host> hosts = new ArrayList<>();
        for (String h : oshost) {
            Host host = new Host();
            host.setName(h);
        }

        return hosts;
    }
    public List<Server> getVMbyHost(Host host){
        List<Server> servers = (List<Server>) os.compute().servers().listAll(true);
        servers = servers.stream().filter(server -> server.getHost().equals(host.getName())).collect(Collectors.toList());
        return servers;
    }
    @Override
    public boolean isStarted() {
        try{
            os.supportsCompute();
            return true;
        }
        catch (ConnectionException e){
            return false;
        }

    }

    @Override
    public List<EngineRole> bindServers(List<EngineRole> roles) {
        List<Server> servers = (List<Server>) os.compute().servers().list();
        for (Server s : servers) {
            EngineRole r = roles.stream()
                    .filter(engineRole -> engineRole.getEngine().getIp().equals(s.getAccessIPv4()))
                    .findFirst().get();
            r.setContainerName(s.getName());
        }

        return roles;
    }
}
