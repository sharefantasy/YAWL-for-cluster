package org.yawlfoundation.cluster.backend.service;

import net.dongliu.requests.Requests;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yawlfoundation.cluster.backend.ZkClientFactory;
import org.yawlfoundation.cluster.backend.service.monitor.EngineVO;
import org.yawlfoundation.cluster.backend.service.monitor.MonitorService;
import org.yawlfoundation.cluster.backend.service.monitor.ResourceStat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by fantasy on 2016/8/6.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger _logger = Logger.getLogger(MonitorServiceImpl.class);
    private CuratorFramework client;

	private String engineNamespace;
	private String serviceNamespace;
	private String monitorPath;
	private String monitorAddress;

	@Autowired
	private ContainerService containerService;
    private PathChildrenCache masterCache;
    private PathChildrenCache slaveCache;
    private PathChildrenCache instanceCache;

	@Autowired
    public MonitorServiceImpl(ZkClientFactory factory,
                              ContainerService containerService,
                              @Value("${zk.connection}") String connection,
                              @Value("${zk.service.namespace}") String serviceNamespace,
                              @Value("${zk.engine.namespace}") String engineNamespace,
                              @Value("${service.monitor.path}") String monitorPath,
                              @Value("${service.monitor.address}") String monitorAddress) {
        this.containerService = containerService;
        this.serviceNamespace = serviceNamespace;
		this.engineNamespace = engineNamespace;
		this.monitorPath = monitorPath;
		this.monitorAddress = monitorAddress;

		client = factory.buildNewClient(connection, serviceNamespace);
		try {
			client.create().creatingParentsIfNeeded().forPath(monitorPath, monitorAddress.getBytes());
			client.usingNamespace(engineNamespace).create().creatingParentsIfNeeded().forPath(monitorPath,
					monitorAddress.getBytes());
			PathChildrenCache cache = new PathChildrenCache(client, engineNamespace, true);
			cache.getListenable().addListener((client1, event) -> cache.rebuild());

            masterCache = new PathChildrenCache(client, engineNamespace + "/role", true);
            slaveCache = new PathChildrenCache(client, engineNamespace + "/slave", true);
            instanceCache = new PathChildrenCache(client, engineNamespace + "/slave", true);

            masterCache.getListenable().addListener((client1, event) -> {
                switch (event.getType()) {
                    case CHILD_REMOVED:
                        String[] paths = event.getData().getPath().split("/");
                        String role = paths[paths.length - 1];
                        ChildData chosen = slaveCache.getCurrentData().get(0);
                        EngineVO engine = new EngineVO();
                        engine.setEngine_id(new String(chosen.getData()));
                        migrate("", engine, role);
                        break;
                }
            }, Executors.newSingleThreadExecutor());
        } catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

    @Override
    public ResourceStat list(){
		PathChildrenCache instanceCache = new PathChildrenCache(client, engineNamespace + "/engine", true);
		PathChildrenCache roleCache = new PathChildrenCache(client, engineNamespace + "/role", true);
		PathChildrenCache slaveCache = new PathChildrenCache(client, engineNamespace + "/slave", true);
		ResourceStat stat = new ResourceStat();
		List<ResourceStat.MasterStat> masterStats = new ArrayList<>();
		List<ResourceStat.SlaveStat> slaveStats = new ArrayList<>();
		for (ChildData c: roleCache.getCurrentData()){
			ResourceStat.MasterStat masterStat = new ResourceStat.MasterStat();
			masterStat.setRole(c.getPath().substring(1));
			String id = new String(c.getData());
			masterStat.setEngineId(id);
			ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + id);
			masterStat.setAddress(new String(engineData.getData()));
			masterStat.setContainerHandler(containerService.getHandlerByEngineId(id));
			masterStats.add(masterStat);
		}
		for (ChildData c: slaveCache.getCurrentData()){
			ResourceStat.SlaveStat slaveStat = new ResourceStat.SlaveStat();
			slaveStat.setSlaveId(c.getPath().substring(1));
			String id = new String(c.getData());
			slaveStat.setEngineId(id);
			ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + id);
			slaveStat.setAddress(new String(engineData.getData()));
			slaveStat.setContainerHandler(containerService.getHandlerByEngineId(id));
			slaveStats.add(slaveStat);
		}
		stat.setMasters(masterStats);
		stat.setSlaves(slaveStats);
		return stat;
	}


	@Override
	public String shutdown(String sessionHandler, EngineVO engine) {
        PathChildrenCache instanceCache = new PathChildrenCache(client, engineNamespace, true);
        ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + engine.getEngine_id());
        String address = new String(engineData.getData());
        Map<String, String> map = new HashMap<>();
        map.put("action", "shutdown");
        map.put("engine", engine.getEngine_id());
        return Requests.post(address).params(map).send().readToText();
    }

	@Override
    public String migrate(String sessionHandler, EngineVO engine, String role) {
        PathChildrenCache instanceCache = new PathChildrenCache(client, engineNamespace, true);
        ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + engine.getEngine_id());
        String address = new String(engineData.getData());
        Map<String, String> map = new HashMap<>();
        map.put("action", "migrate");
        map.put("engine", engine.getEngine_id());
        map.put("role", role);
        return Requests.post(address).params(map).send().readToText();
    }

	@Override
    public String restore(String sessionHandler, EngineVO engine) {
        PathChildrenCache instanceCache = new PathChildrenCache(client, engineNamespace, true);
        ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + engine.getEngine_id());
        String address = new String(engineData.getData());
        Map<String, String> map = new HashMap<>();
        map.put("action", "restore");
        map.put("engine", engine.getEngine_id());
        return Requests.post(address).params(map).send().readToText();
    }

	@Override
    public String exile(String sessionHandler, EngineVO engine) {
        PathChildrenCache instanceCache = new PathChildrenCache(client, engineNamespace, true);
        ChildData engineData = instanceCache.getCurrentData(engineNamespace + "/engine/" + engine.getEngine_id());
        String address = new String(engineData.getData());
        Map<String, String> map = new HashMap<>();
        map.put("engine", engine.getEngine_id());
        return Requests.post(address).params(map).send().readToText();
    }
}
