package org.yawlfoundation.cluster.naming.service;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by fantasy on 2016/7/26.
 */
@Component
public class ZkClientFactory implements DisposableBean {

	private CuratorFramework client;

	@Value("${zk.connection}")
	private String connectionString;
	@Value("${zk.namespace}")
	private String namespace;

	public CuratorFramework getClient() {
		return client;
	}

	@PostConstruct
	public CuratorFramework buildNewClient() {
		buildNewClient(namespace);
		return client;
	}

	public CuratorFramework buildNewClient(String namespace) {
		this.namespace = namespace;
		client = CuratorFrameworkFactory.builder().connectString(connectionString)
				.retryPolicy(new ExponentialBackoffRetry(100, Integer.MAX_VALUE)).namespace(namespace).build();
		client.start();
		return client;
	}

	public String getNamespace() {
		return namespace;
	}

	@Override
	public void destroy() throws Exception {
		client.close();
	}
}
