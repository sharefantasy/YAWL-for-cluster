package org.scheduleModule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by fantasy on 2016/5/24.
 */
@Document
public class User {
    @Id
    private String id;
    private String userName;
    private String password;
    private Tenant owner;

    public User() {
    }

    public User(String userName, String password, Tenant owner) {
        this.userName = userName;
        this.password = password;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Tenant getOwner() {
        return owner;
    }

    public void setOwner(Tenant owner) {
        this.owner = owner;
    }
}
