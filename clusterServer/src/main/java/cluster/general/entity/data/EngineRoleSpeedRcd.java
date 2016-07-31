package cluster.general.entity.data;

import cluster.general.entity.EngineRole;

/**
 * Created by fantasy on 2015/9/2.
 */
public class EngineRoleSpeedRcd extends SpeedRcd {
	private EngineRole role;

	public EngineRole getRole() {
		return role;
	}

	public void setRole(EngineRole role) {
		this.role = role;
	}
}
