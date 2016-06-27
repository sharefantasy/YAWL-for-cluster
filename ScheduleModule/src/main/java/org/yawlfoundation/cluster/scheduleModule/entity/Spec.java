package org.yawlfoundation.cluster.scheduleModule.entity;

import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * Created by fantasy on 2016/5/11.
 */
public class Spec {
    @Id
    private String specid;
    private Tenant owner;
    private String version;
    private String uri;
    private Map<Engine, Integer> internalIds;

    public Spec() {
    }

    public Spec(Tenant owner, String specid, String version, String uri) {
        this.owner = owner;
        this.specid = specid;
        this.version = version;
        this.uri = uri;
    }

    public Tenant getOwner() {
        return owner;
    }

    public void setOwner(Tenant owner) {
        this.owner = owner;
    }

    public Map<Engine, Integer> getInternalIds() {
        return internalIds;
    }

    public void setInternalIds(Map<Engine, Integer> internalIds) {
        this.internalIds = internalIds;
    }

    public String getSpecid() {
        return specid;
    }

    public void setSpecid(String specid) {
        this.specid = specid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
