package cluster.general.entity.data;

import cluster.general.entity.EngineRole;

import java.util.Date;

/**
 * Created by fantasy on 2016/2/21.
 */

public class SpeedRcd implements Comparable {
	private long id;
	private EngineRole role;
	private Date time;
	private double speed;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public int compareTo(Object o) {
		SpeedRcd to = (SpeedRcd) o;
		return time.compareTo(to.time);
	}

	public EngineRole getRole() {
		return role;
	}

	public void setRole(EngineRole role) {
		this.role = role;
	}
}
