package cluster.general.entity;

import cluster.general.entity.data.EngineRoleSpeedRcd;

import java.util.Date;
import java.util.Set;

public class EngineRole{
    private long id;
    private Engine engine;

    private String role;
    private String containerName;
    //quantifier
    private Date currentRcdTime;
    private double currentSpeed;
    private Set<EngineRoleSpeedRcd> speedRcds;

    //scheduler related
    private Host host;
    private Tenant tenant;

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

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
    public Engine getEngine() {
        return engine;
    }
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<EngineRoleSpeedRcd> getSpeedRcds() {
        return speedRcds;
    }

    public void setSpeedRcds(Set<EngineRoleSpeedRcd> speedRcds) {
        this.speedRcds = speedRcds;
    }
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Date getCurrentRcdTime() {
        return currentRcdTime;
    }

    public void setCurrentRcdTime(Date currentRcdTime) {
        this.currentRcdTime = currentRcdTime;
    }

    public boolean equals(EngineRole e) {
        return role.equals(e.role);
    }

    public String toString() {
        return role;
    }
}