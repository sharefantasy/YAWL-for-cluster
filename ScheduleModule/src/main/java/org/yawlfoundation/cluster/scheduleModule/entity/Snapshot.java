package org.yawlfoundation.cluster.scheduleModule.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

/**
 * Created by fantasy on 2016/7/9.
 */
@Document
public class Snapshot {
	@Id
	private String id;
	private Date recordTime;
	private long caseCount;
	private long workItemCount;
	private String engine_id;

	public Snapshot() {
	}

	public Snapshot(Date recordTime, long caseCount, long workItemCount, String engine_id) {
		this.recordTime = recordTime;
		this.caseCount = caseCount;
		this.workItemCount = workItemCount;
		this.engine_id = engine_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	public String getEngine() {
		return engine_id;
	}

	public void setEngine(String engine_id) {
		this.engine_id = engine_id;
	}

	public long getWorkItemCount() {
		return workItemCount;
	}

	public void setWorkItemCount(long workItemCount) {
		this.workItemCount = workItemCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Snapshot))
			return false;
		Snapshot snapshot = (Snapshot) o;
		return Objects.equals(recordTime, snapshot.recordTime) && Objects.equals(engine_id, snapshot.engine_id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(recordTime, engine_id);
	}

	@Override
	public String toString() {
		return "Snapshot{" + "id='" + id + '\'' + ", recordTime=" + recordTime + ", caseCount=" + caseCount
				+ ", workItemCount=" + workItemCount + ", engine_id='" + engine_id + '\'' + '}';
	}

	public static Snapshot fromJSON(String jsonString, String engine_id) {
		JSONObject object = JSON.parseObject(jsonString);
		Date date = new Date((Long) object.get("timestamp"));
		return new Snapshot(date, (Long) object.get("caseCount"), (Long) object.get("workItemCount"), engine_id);
	}
}
