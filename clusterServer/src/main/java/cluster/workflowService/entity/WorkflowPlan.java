package cluster.workflowService.entity;

import cluster.workflowService.entity.data.OperationalStatistics;
import cluster.workflowService.entity.data.SettlementStatistics;

import java.util.Date;
import java.util.List;

/**
 * Created by fantasy on 2016/2/12.
 */
public class WorkflowPlan {
	private long id;
	private String name;
	private Date startTime;
	private Date endTime;
	private long operateInterval;
	private long operateTimes;
	private SettlementStatistics currentStatistics;
	private List<OperationalStatistics> opStatistics;
	private boolean working;

	public List<OperationalStatistics> getOpStatistics() {
		return opStatistics;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getOperateInterval() {
		return operateInterval;
	}

	public void setOperateInterval(long operateInterval) {
		this.operateInterval = operateInterval;
	}

	public long getOperateTimes() {
		return operateTimes;
	}

	public void setOperateTimes(long operateTimes) {
		this.operateTimes = operateTimes;
	}

	public SettlementStatistics getCurrentStatistics() {
		return currentStatistics;
	}

	public void setCurrentStatistics(SettlementStatistics currentStatistics) {
		this.currentStatistics = currentStatistics;
	}

	public void setOpStatistics(List<OperationalStatistics> opStatistics) {
		this.opStatistics = opStatistics;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}
}
