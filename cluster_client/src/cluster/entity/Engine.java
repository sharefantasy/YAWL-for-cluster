package cluster.entity;

import java.util.*;

/**
 * Created by fantasy on 2015/8/22.
 */

public class Engine {
    //engine private identity
    private String engineID;
    private String password;

    //HA related
    private EngineRole engineRole;
    private Date lastHeartbeatTime;
    private Date lastLogineTime;
    private EngineStatus status;




    public Engine(){}
    public Engine(String engineID, String password){
        this.engineID = engineID;
        this.password = password;
        this.status = EngineStatus.INACTIVE;
    }
    public String getEngineID() {
        return engineID;
    }
    public void setEngineID(String engineID) {
        this.engineID = engineID;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }
    public void setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }
    public EngineStatus getStatus() {
        return status;
    }
    public void setStatus(EngineStatus status) {
        this.status = status;
    }
    public EngineRole getEngineRole() {
        return engineRole;
    }
    public void setEngineRole(EngineRole engineRole) {
        this.engineRole = engineRole;
//        engineRole.setEngine(this);
    }

    public Date getLastLogineTime() {
        return lastLogineTime;
    }
    public void setLastLogineTime(Date lastLogineTime) {
        this.lastLogineTime = lastLogineTime;
    }

    public void roleTaking(Engine changer){
        engineRole = changer.engineRole;
        changer.setEngineRole(null);
    }
    public void clearLost(){
        lastHeartbeatTime = new Date();
    }

    public void addSpeed(Date newDate, double newSpeed){
        engineRole.updateSpeed(newDate, newSpeed);
    }
}
