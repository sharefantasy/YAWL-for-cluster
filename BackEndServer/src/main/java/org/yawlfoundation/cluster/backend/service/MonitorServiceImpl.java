package org.yawlfoundation.cluster.backend.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yawlfoundation.cluster.backend.ZkClientFactory;
import org.yawlfoundation.cluster.backend.service.monitor.EngineVO;
import org.yawlfoundation.cluster.backend.service.monitor.InterfaceC_ClusterSideCommand;
import org.yawlfoundation.cluster.backend.service.monitor.ResourceStat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 2016/8/6.
 */
@Service
public class MonitorService implements InterfaceC_ClusterSideCommand {

	private static final Logger _logger = Logger.getLogger(MonitorService.class);
	private CuratorFramework client;

	private String engineNamespace;
	private String serviceNamespace;
	private String monitorPath;
	private String monitorAddress;

	@Autowired
	private ContainerService containerService;

	@Autowired
	public MonitorService(ZkClientFactory factory, @Value("${zk.connection}") String connection,
			@Value("${zk.service.namespace}") String serviceNamespace,
			@Value("${zk.engine.namespace}") String engineNamespace,
			@Value("${service.monitor.path") String monitorPath,
			@Value("${service.monitor.address") String monitorAddress) {

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
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

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
		return null;
	}

	@Override
	public String migrate(String sessionHandler, EngineVO engineVO, String role) {
		return null;
	}

	@Override
	public String restore(String sessionHandler, EngineVO engineVO) {
		return null;
	}

	@Override
	public String exile(String sessionHandler, EngineVO engineVO) {
		return null;
	}
}
