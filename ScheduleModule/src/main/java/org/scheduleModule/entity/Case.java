package org.scheduleModule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by fantasy on 2016/5/11.
 */
@Document
public class Case {
    @Id
    private String id;
    private String internalId;
    private Engine engine;
    private Tenant tenant;

    public Case() {
    }

    public Case(String internalId, Engine engine) {
        this.internalId = internalId;
        this.engine = engine;
        this.tenant = engine.getTenant();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
