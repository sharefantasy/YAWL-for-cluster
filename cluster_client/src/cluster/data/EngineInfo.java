package cluster.data;

import java.util.Date;

/**
 * Created by fantasy on 2015/8/22.
 */
public class EngineInfo {
    private String engineID;
    private String Identifier;
    private Date lastHeartbeatTime;
    public EngineInfo(String engineID, String identifier){
        this.engineID = engineID;
        this.Identifier = identifier;
    }
    public String getEngineID() {
        return engineID;
    }
    public void setEngineID(String engineID) {
        this.engineID = engineID;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void clearLost(){
        lastHeartbeatTime = new Date();
    }

    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }
}
