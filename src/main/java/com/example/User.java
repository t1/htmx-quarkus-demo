package com.example;

import jakarta.enterprise.context.SessionScoped;
import lombok.Data;

import java.io.Serializable;

@SessionScoped @Data
public class User implements Serializable {
    private String id;
    private String name;

    public boolean isLoggedIn() {return id != null;}

    public void login(String userId) {
        this.id = userId;
        this.name = Character.toTitleCase(userId.charAt(0)) + userId.substring(1);
    }

    public void logout() {
        this.id = null;
        this.name = null;
    }
}
