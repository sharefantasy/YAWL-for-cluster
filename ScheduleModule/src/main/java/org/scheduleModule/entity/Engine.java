package org.scheduleModule.entity;


import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by fantasy on 2016/5/10.
 */

public class Engine implements Serializable {
    @Id
    private String id;
    private String address;
    private int port;
    private Tenant tenant;

    public Engine() {
    }

    public Engine(String address, int port, Tenant tenant) {
        this.address = address;
        this.port = port;
        this.tenant = tenant;
    }

    public Engine(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return String.format("Engine(%s){address='%s', port=%d}", id, address, port);
    }
}
