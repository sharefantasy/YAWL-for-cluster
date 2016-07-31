package cluster.general.entity.data;

import cluster.general.entity.Host;

/**
 * Created by fantasy on 2016/2/21.
 */
public class HostSpeedRcd extends SpeedRcd {
	private Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
}
