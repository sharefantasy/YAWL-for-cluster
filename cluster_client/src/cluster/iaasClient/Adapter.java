package cluster.iaasClient;

/**
 * Created by fantasy on 2016/1/4.
 */
public interface Adapter {
    void Migrate (VM vm, Host dest, Host src) throws MigrationException;
    Host HostUsage(String hostID);
}
