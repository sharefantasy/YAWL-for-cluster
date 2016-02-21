package cluster.util.iaasClient;

import cluster.general.entity.EngineRole;
import cluster.general.entity.Host;
import cluster.util.exceptions.MigrationException;

import java.util.List;

/**
 * Created by fantasy on 2016/1/4.
 */

public interface Adapter {
    void Migrate (EngineRole vm, Host dest) throws MigrationException;
//    Engine HostUsage(String hostID);
//    Map<Host, EngineRole> loadEngineHostMap();
    List<Host> getHosts();
    boolean isStarted();
    void addObserver(envObserver ob);
    void notifyStart();
    void notifyShutdown();

    List<EngineRole> bindServers(List<EngineRole> roles);
}
