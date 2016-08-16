package org.yawlfoundation.plugin.HA;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by fantasy on 2016/7/26.
 */
@Component
public class ZkClientFactory {
	private String namespace;
	private CuratorFramework client;
	public ZkClientFactory(@Value("${zk.connection}") String connectionString,
			@Value("${zk.namespace}") String namespace) {
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
