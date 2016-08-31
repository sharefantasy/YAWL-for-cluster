package org.yawlfoundation.cluster.backend.service.monitor;


/**
 * Created by fantasy on 2016/7/18.
 */
public interface InterfaceC_ClusterSideCommand {


	/**
	 * Shutdown string.
	 *
	 * @param sessionHandler the session handler
	 * @param engine         the engine
	 * @return result of shutdown
	 */
	String shutdown(String sessionHandler, EngineVO engine);


	/**
	 * Migrate.
	 *
	 * @param sessionHandler the session handler
	 * @param engineVO       the engine vo
	 * @param role           the new role for
	 * @return the string
	 */
	String migrate(String sessionHandler, EngineVO engineVO, String role);

	/**
	 * Restore.
	 *
	 * @param sessionHandler the session handler
	 * @param engineVO       the engine vo
	 * @return the string
	 */
	String restore(String sessionHandler, EngineVO engineVO);

	/**
	 * Exile.
	 *
	 * @param sessionHandler the session handler
	 * @param engineVO       the engine vo
	 * @return the string
	 */
	String exile(String sessionHandler, EngineVO engineVO);
}
