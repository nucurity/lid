package com.projects.lid.entities;

/**
 * Created by nucur on 4/9/2017.
 */

public class User {

    private static User instance;

    private User(){

    }

    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }

    private String userId;
    private String groupId;
    private String username;
    private String password;
    private String name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
