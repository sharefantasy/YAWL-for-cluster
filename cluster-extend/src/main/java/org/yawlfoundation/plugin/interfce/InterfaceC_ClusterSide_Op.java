package org.yawlfoundation.plugin.interfce;

import org.yawlfoundation.plugin.interfce.vo.EngineVO;

/**
 * Created by fantasy on 2016/7/18.
 */
public interface InterfaceC_ClusterSide_Op {
	void shutdown(String sessionHandler, EngineVO engine);

    void migrate(String sessionHandler, EngineVO engineVO, String role);

    void restore(String sessionHandler, EngineVO engineVO);
}
