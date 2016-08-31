package org.yawlfoundation.cluster.monitor;

import org.yawlfoundation.cluster.backend.service.monitor.EngineVO;
import org.yawlfoundation.cluster.backend.service.monitor.ResourceStat;

/**
 * Created by fantasy on 2016/8/19.
 */
public interface MonitorClient {

    /**
     * List resource stat.
     *
     * @return the resource stat
     */
    ResourceStat list();

    /**
     * Shutdown string.
     *
     * @param engine the engine
     * @return result of shutdown
     */
    String shutdown(EngineVO engine);


    /**
     * Migrate.
     *
     * @param engineVO the engine vo
     * @param role     the new role for
     * @return the string
     */
    String migrate(EngineVO engineVO, String role);

    /**
     * Restore.
     *
     * @param engineVO the engine vo
     * @return the string
     */
    String restore(EngineVO engineVO);

    /**
     * Exile.
     *
     * @param engineVO the engine vo
     * @return the string
     */
    String exile(EngineVO engineVO);
}
