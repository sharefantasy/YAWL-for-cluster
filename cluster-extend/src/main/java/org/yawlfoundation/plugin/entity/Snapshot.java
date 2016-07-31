package org.yawlfoundation.plugin.entity;

/**
 * Created by fantasy on 2016/7/14.
 */
public class Snapshot {
	private long timestamp;
	private long caseCount;
	private long workItemCount;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	public long getWorkItemCount() {
		return workItemCount;
	}

	public void setWorkItemCount(long workItemCount) {
		this.workItemCount = workItemCount;
	}
	public String toJSON() {
		return String.format("{\"timestamp\": %d,\"caseCount\": %d,\"workItemCount\": %d}", timestamp, caseCount,
				workItemCount);
	}
}
