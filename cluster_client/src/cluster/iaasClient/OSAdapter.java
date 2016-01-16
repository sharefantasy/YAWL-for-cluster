package cluster.iaasClient;

import cluster.ditribute.strategy.SchedulerStatus;
import cluster.entity.Engine;
import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.event.exceptions.MigrationException;
import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.actions.LiveMigrateOptions;
import org.openstack4j.openstack.OSFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 2016/1/4.
 */
public class OSAdapter extends BaseAdapter{
    private static final Logger _logger = Logger.getLogger(OSAdapter.class);
    private ArrayList<envObserver> obs = new ArrayList<>();
    private SchedulerStatus status;
    private OSClient os;
    private static OSAdapter instance;
    private OSAdapter(){
        String OS_AUTH_URL = "http://192.168.0.4:5000/v2.0/";
        String OS_AUTH_NAME = "admin";
        String OS_AUTH_PASSWORD = "password";
        String OS_AUTH_PROJECT = "demo";
        os = OSFactory.builder()
                .endpoint(OS_AUTH_URL)
                .credentials(OS_AUTH_NAME, OS_AUTH_PASSWORD)
                .tenantName(OS_AUTH_PROJECT)
                .authenticate();

    }
    public static OSAdapter getInstance(){
        if (instance == null){
            instance = new OSAdapter();
        }
        return instance;
    }
    @Override
    public void Migrate(EngineRole engine, Host dest) throws MigrationException {
        // TODO: 2016/1/11 VM migration needs further configurations. Check OS confuguration files to implement this method

        os.compute().migrations().list().get(0).getDestNode();
        os.compute().servers()
                .liveMigrate(engine.getContainerName(),
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

        return null;
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
}
