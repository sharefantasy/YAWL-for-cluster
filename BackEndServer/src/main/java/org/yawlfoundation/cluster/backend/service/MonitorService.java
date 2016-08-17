package org.yawlfoundation.cluster.backend.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yawlfoundation.cluster.backend.ZkClientFactory;

/**
 * Created by fantasy on 2016/8/6.
 */
@Service
public class MonitorService {

	private static final Logger _logger = Logger.getLogger(MonitorService.class);
	private CuratorFramework client;

	@Autowired
	public MonitorService(ZkClientFactory factory, @Value("${zk.connection}") String connection,
			@Value("${zk.service.namespace}") String serviceNamespace,
			@Value("${zk.engine.namespace}") String engineNamespace,
			@Value("${service.monitor.path") String monitorPath,
			@Value("${service.monitor.address") String monitorAddress) {
		client = factory.buildNewClient(connection, serviceNamespace);
		try {
			client.create().creatingParentsIfNeeded().forPath(monitorPath, monitorAddress.getBytes());
			client.usingNamespace(engineNamespace).create().creatingParentsIfNeeded().forPath(monitorPath,
					monitorAddress.getBytes());
			PathChildrenCache cache = new PathChildrenCache(client, engineNamespace, true);
			cache.getListenable().addListener((client1, event) -> {

			});
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}
}
