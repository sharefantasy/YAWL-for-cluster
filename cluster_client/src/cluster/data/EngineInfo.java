package cluster.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fantasy on 2015/8/22.
 */

public class EngineInfo {
    private String engineID;

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
    private String engineRole;
    private Date lastHeartbeatTime;
    private Date lastLogineTime;
    private EngineStatus status;
    public EngineInfo(){}
    public EngineInfo(String engineID, String password){
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

    public String getEngineRole() {
        return engineRole;
    }
    public void setEngineRole(String engineRole) {
        this.engineRole = engineRole;
    }


    public Date getLastLogineTime() {
        return lastLogineTime;
    }
    public void setLastLogineTime(Date lastLogineTime) {
        this.lastLogineTime = lastLogineTime;
    }

    public void roleTaking(EngineInfo changer){
        engineRole = changer.engineRole;
        engineRole = null;
    }
    public void clearLost(){
        lastHeartbeatTime = new Date();
    }
}
