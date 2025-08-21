package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
    private String username;
    private int permission;
    private int store;

    public User() { }

    public User(String username, int permission) {
        this.username = username;
        this.permission = permission;
    }

    public User(String username, int permission, int store) {
        this.username = username;
        this.permission = permission;
        this.store = store;
    }

    public String getUsername() {
        return username;
    }

    public int getPermission() {
        return permission;
    }

    public int getStore() {
        return store;
    }
}
