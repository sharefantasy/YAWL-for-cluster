package cluster.entity;

import java.util.*;

/**
 * Created by fantasy on 2015/9/2.
 */
public class EngineRole{
    public static final EngineRole IDLE = new EngineRole("idle");
    private Engine engine;
    private String role;
    private String containerName;
    //quantifier
    private Date recordTime = new Date();
    private double currentSpeed;
    private Map<Date, Double> historySpeed = new HashMap<>();

    //scheduler related
    private Host host;
    private Tenant tenant;

    public EngineRole(){
        role = UUID.randomUUID().toString();
    }
    public EngineRole(String role){
        this.role = role;

    }
    public boolean equals(EngineRole e){
        return role.equals(e.role);
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

    public String toString(){return role;}

    public void updateSpeed(Date newDate, double newSpeed){
        historySpeed.put(recordTime, currentSpeed);
        recordTime = newDate;
        currentSpeed = newSpeed;
    }

    public Map<Date, Double> getHistorySpeed() {
        return historySpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
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
}