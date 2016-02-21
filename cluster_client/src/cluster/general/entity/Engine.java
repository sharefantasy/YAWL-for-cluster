package cluster.general.entity;

import java.util.*;

/**
 * Created by fantasy on 2015/8/22.
 */

public class Engine {
    //engine private identity
    private long id;
    private String engineID;
    private String password;
    private String address;
    private String ip;
    //HA related
    private EngineRole engineRole;
    private Date lastHeartbeatTime;
    private Date lastLoginTime;
    private EngineStatus status;

    public Engine(){}
    public Engine(String engineID, String password){
        this.engineID = engineID;
        this.password = password;
        this.status = EngineStatus.INACTIVE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEngineID() {
        return engineID;
    }
    public void setEngineID(String engineID) {
        this.engineID = engineID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
