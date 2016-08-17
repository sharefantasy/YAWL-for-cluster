package org.yawlfoundation.cluster.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by fantasy on 2016/8/17.
 */
@ConfigurationProperties("zk")
public class zkProperties {
	private String connection;
	private String namespace;

	private static class Service {
		private String namespace;

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;

		}
	}
	private static class Engine {
		private String namespace;

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
