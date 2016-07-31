package org.yawlfoundation.plugin.HA;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yawlfoundation.plugin.persistX.DynamicSourcePersistenceManager;

import java.util.concurrent.Executors;

/**
 * Created by fantasy on 2016/7/26.
 */
@Service
public class HAService {

	public HA_Status getCurrentStatus() {
		return currentStatus;
	}
	public enum HA_Status {
		NOT_WOKING, MASTERING, SLAVING, MIGRATING
	};
	private HA_Status currentStatus;
	private static final Logger _logger = Logger.getLogger(HAService.class);

	private final CuratorFramework client;
	private final DynamicSourcePersistenceManager _manager;
	private String slavePath;

	public HAService(@Autowired ZkClientFactory factory) {
		this._manager = DynamicSourcePersistenceManager.getInstance();
		this.client = factory.getClient();
		try {
			final NodeCache nodeCache = new NodeCache(client, "/monitor");
			nodeCache.getListenable().addListener(new NodeCacheListener() {
				@Override
				public void nodeChanged() throws Exception {
					String monitor = new String(nodeCache.getCurrentData().getData());
					System.out.println("new monitor address " + monitor);
				}
			}, Executors.newSingleThreadExecutor());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String follow() {
		try {
			slavePath = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
					.forPath("/slave/slave-");
			_manager.setFollow();
			currentStatus = HA_Status.SLAVING;
			return "success";
		} catch (Exception e) {
			String message = "client could not create slave node, please check if zk server is up.";
			_logger.error(message);
			currentStatus = HA_Status.MIGRATING;
			return message;
		}
	}
	protected String master(String role) {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/role/" + role,
					role.getBytes());
		} catch (Exception e) {
			String message = "client could not create master node, please check if zk server is up.";
			_logger.error(message);
			currentStatus = HA_Status.SLAVING;
			return message;
		}
		if (_manager.setMaster(role)) {
			currentStatus = HA_Status.MASTERING;
			return "success";
		}
		return "failed";
	}
	public String coupDetat(String role) {
		if (role.equals(_manager.getEngineRole())) {
			return "already";
		}
		try {
			currentStatus = HA_Status.MIGRATING;
			String currentRole = _manager.getEngineRole();
			if (client.checkExists().forPath(slavePath) != null) {
				client.delete().guaranteed().forPath(slavePath);
			} else if (client.checkExists().forPath("/role/" + currentRole) != null) {
				client.delete().guaranteed().forPath("/role/" + currentRole);
			} else {
				_logger.warn(slavePath + " is deleted. cluster might be in a inconsistent state.");
			}
			return master(role);
		} catch (Exception e) {
			String message = "failed to get role " + role;
			_logger.error(message);
			return message;
		}
	}
	public String exile() {
		if (_manager.getEngineRole() == null) {
			return "already";
		}
		currentStatus = HA_Status.MIGRATING;
		try {
			String role = _manager.getEngineRole();
			if (client.checkExists().forPath("/role/" + role) != null) {
				client.delete().guaranteed().forPath("/role/" + role);
			} else {
				_logger.warn("role " + role + " is deleted. cluster might be in a inconsistent state.");
			}
		} catch (Exception e) {
			_logger.error("delete role " + _manager.getEngineRole() + " failed");
			return "exile failed";
		}
		return follow();
	}
	public String getMonitorServerAddress() throws Exception {
		if (client.getState().equals(CuratorFrameworkState.STARTED)) {
			if (client.checkExists().forPath("/monitor") != null) {
				return new String(client.getData().forPath("/monitor"));
			}
			return null;
		} else {
			throw new IllegalStateException("zk server is not started");
		}
	}
}
