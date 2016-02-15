package cluster.gateway.service;

import cluster.entity.Engine;
import cluster.iaasClient.Adapter;
import org.openstack4j.model.compute.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fantasy on 2016/2/13.
 */
@Service("engineService")
public class EngineService {
    @Autowired
    private Adapter adapter;

    public List<String> getEngineVMname() {
        List<Server> servers = adapter.getServers();
        return servers.stream().map(Server::getName).collect(Collectors.toList());
    }


}
