package org.yawlfoundation.plugin.HA;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fantasy on 2016/7/26.
 */
public class ZkClientFactory {
	private String namespace;
	private CuratorFramework client;
	public ZkClientFactory(String connectionString, String namespace) {
		buildNewClient(connectionString, namespace);
	}
	public CuratorFramework getClient() {
		return client;
	}
	public CuratorFramework buildNewClient(String connectionString, String namespace) {
		this.namespace = namespace;
		client = CuratorFrameworkFactory.builder().connectString(connectionString)
				.retryPolicy(new ExponentialBackoffRetry(100, Integer.MAX_VALUE)).namespace(namespace).build();
		client.start();
		return client;
	}

	public String getNamespace() {
		return namespace;
	}
}
