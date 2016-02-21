package cluster.general.entity;

import cluster.general.entity.data.HostSpeedRcd;
import cluster.general.entity.data.TenantSpeedRcd;
import org.yawlfoundation.yawl.util.HibernateEngine;

import java.util.*;

/**
 * Created by fantasy on 2016/1/6.
 */
public class Tenant {


    private long id;
    private String name;
    private Date createTime;
    private double SLOspeed;
    private double currentSpeed;
    private Date recordTime;
    private Set<TenantSpeedRcd> speedRcds;
    private List<EngineRole> engineList;

    public double getSLOspeed() {
        return SLOspeed;
    }

    public void setSLOspeed(double SLOspeed) {
        this.SLOspeed = SLOspeed;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }


    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EngineRole> getEngineList() {
        return engineList;
    }

    public void setEngineList(List<EngineRole> engineList) {
        this.engineList = engineList;
    }

    public Set<TenantSpeedRcd> getSpeedRcds() {
        return speedRcds;
    }

    public void setSpeedRcds(Set<TenantSpeedRcd> speedRcds) {
        this.speedRcds = speedRcds;
    }


}
