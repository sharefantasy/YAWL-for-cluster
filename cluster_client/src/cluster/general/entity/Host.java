package cluster.general.entity;

import cluster.general.entity.data.HostCapability;
import cluster.general.entity.data.HostSpeedRcd;
import cluster.util.exceptions.GeneralException;

import java.util.*;

/**
 * Created by fantasy on 2016/1/5.
 */


public class Host {

    private long id;
    private String name;
    private String ip;
    private List<EngineRole> engineList;
    private Set<HostCapability> capabilitySet;
    private Set<HostSpeedRcd> speedRcds;

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    private double currentSpeed;


    private Date recordTime;

    public Set<HostCapability> getCapabilitySet() {
        return capabilitySet;
    }

    public void setCapabilitySet(Set<HostCapability> capabilitySet) {
        this.capabilitySet = capabilitySet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<EngineRole> getEngineList() {
        return engineList;
    }
    public void setEngineList(List<EngineRole> engineList) {
        this.engineList = engineList;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public double getCapability(int engineNumber){
        Optional<HostCapability> c1 = (capabilitySet.stream().filter((c)->c.geteNum() == engineNumber).findFirst());
        if(c1.isPresent()){
            return c1.get().getCapability();
        }
        capabilitySet.add(new HostCapability(this,engineNumber));
        return 0;
    }
    public void setCapability(int engineNumber, double capability){
        Optional<HostCapability> c1 = (capabilitySet.stream().filter((c)->c.geteNum() == engineNumber).findFirst());
        if(c1.isPresent()){
            c1.get().setCapability(capability);
        }
        capabilitySet.add(new HostCapability(this,engineNumber));
    }



    public boolean equals(Host host){
        return this.name.equals(host.name);
    }



    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                '}';
    }


    public Set<HostSpeedRcd> getSpeedRcds() {
        return speedRcds;
    }

    public void setSpeedRcds(Set<HostSpeedRcd> speedRcds) {
        this.speedRcds = speedRcds;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
