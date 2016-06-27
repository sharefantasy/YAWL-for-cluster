package org.yawlfoundation.cluster.scheduleModule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by fantasy on 2016/5/10.
 */
@Document
public class Tenant {
    @Id
    private String id;
    private String name;
    private Set<String> engineSet;
    private Set<String> userSet;
    private Set<String> SpecSet;
    private String defaultWorklist;

    private void setSets() {
        engineSet = new HashSet<>();
        userSet = new HashSet<>();
        SpecSet = new HashSet<>();
    }

    public Tenant() {
        setSets();
    }

    public Tenant(String defaultWorklist) {
        this.defaultWorklist = defaultWorklist;
        setSets();
    }

    public Tenant(String name, String defaultWorklist) {
        this.name = name;
        this.defaultWorklist = defaultWorklist;
        setSets();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getEngineSet() {
        return engineSet;
    }

    public void setEngineSet(Set<String> engineSet) {
        this.engineSet = engineSet;
    }

    public Set<String> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<String> userSet) {
        this.userSet = userSet;
    }

    public Set<String> getSpecSet() {
        return SpecSet;
    }

    public void setSpecSet(Set<String> specSet) {
        SpecSet = specSet;
    }

    @Override
    public String toString() {
        return String.format("Tenant{id='%s', name='%s'}", id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tenant)) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(id, tenant.id) &&
                Objects.equals(defaultWorklist, tenant.defaultWorklist);
    }

    public String getDefaultWorklist() {
        return defaultWorklist;
    }

    public void setDefaultWorklist(String defaultWorklist) {
        this.defaultWorklist = defaultWorklist;
    }

    public void addEngine(Engine engine) {
        engineSet.add(engine.getId());
    }
}
