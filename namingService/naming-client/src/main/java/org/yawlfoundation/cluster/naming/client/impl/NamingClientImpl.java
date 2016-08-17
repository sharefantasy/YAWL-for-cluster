package org.yawlfoundation.cluster.naming.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.cluster.naming.client.NamingClient;

/**
 * Created by fantasy on 2016/8/10.
 */

public class NamingClientImpl implements NamingClient {

	private CuratorFramework client;
	private String selfAddress;
	private String namingPath = "/naming";
	private String serviceId;
	private PathChildrenCache cache;
	private Map<String, String> serviceMap;
	private static final Logger logger = LoggerFactory.getLogger(NamingClientImpl.class);
	public NamingClientImpl(CuratorFramework client, String serviceId, String serviceAddress) {
		this.client = client;
		this.selfAddress = serviceAddress;
		this.serviceId = serviceId;
		if (!client.getState().equals(CuratorFrameworkState.STARTED)) {
			throw new IllegalArgumentException("zk is not started");
		}
		try {
			if (client.checkExists().forPath(namingPath) != null) {
				this.attach();
				cache = new PathChildrenCache(client, "/service", true);
				cache.getListenable().addListener((client1, event) -> {
					cache.rebuild();
					serviceMap = new HashMap<>();
					for (ChildData d : cache.getCurrentData()) {
						serviceMap.put(d.getPath(), new String(d.getData()));
					}
				}, Executors.newSingleThreadExecutor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public Map<String, String> list() {
		return serviceMap;
	}

	@Override
	public String get(String serviceId) {
		if (serviceId != null) {
			return serviceMap.get(serviceId);
		}
		return null;
	}

	@Override
	public String attach() {
		try {
			client.usingNamespace("service").create().creatingParentsIfNeeded().forPath(serviceId,
					selfAddress.getBytes());
			return "success";
		} catch (Exception e) {
			logger.error("create naming failed");
			return "failed";
		}
	}

	@Override
	public String detach() {
		try {
			client.usingNamespace("service").delete().guaranteed().forPath(serviceId);
			return "success";
		} catch (Exception e) {
			logger.error("delete naming failed");
			return "failed";
		}
	}
}
