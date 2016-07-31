package cluster.general.entity.data;

import cluster.general.entity.Tenant;

/**
 * Created by fantasy on 2016/2/21.
 */
public class TenantSpeedRcd extends SpeedRcd {
	private Tenant tenant;

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
}
