package org.yawlfoundation.yawl.engine.interfce;

/**
 * Created by fantasy on 2016/7/18.
 */
public interface EngineGatewayClusterExtent extends EngineGateway {
	void restore(String session);
	void shutdown(String session);
	String migrateRole(String session, String role);
	String getMigrationStatus(String session);
}
