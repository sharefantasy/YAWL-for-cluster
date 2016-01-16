package cluster.iaasClient;

import cluster.entity.Engine;
import cluster.entity.EngineRole;
import cluster.entity.Host;
import cluster.event.exceptions.MigrationException;

import java.util.List;
import java.util.Map;

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
}
