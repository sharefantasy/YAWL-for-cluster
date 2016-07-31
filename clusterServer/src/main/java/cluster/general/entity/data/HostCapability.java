package cluster.general.entity.data;

import cluster.general.entity.Host;

/**
 * Created by fantasy on 2016/1/20.
 */
public class HostCapability {
	private long id;
	private Host host;
	private int eNum;
	private double capability;

	public HostCapability(Host host, int eNum) {
		this.eNum = eNum;
		this.host = host;
		capability = 0;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public int geteNum() {
		return eNum;
	}

	public void seteNum(int eNum) {
		this.eNum = eNum;
	}

	public double getCapability() {
		return capability;
	}

	public void setCapability(double capability) {
		this.capability = capability;
	}

}
