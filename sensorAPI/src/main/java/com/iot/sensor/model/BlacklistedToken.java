package com.iot.sensor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "token_blacklist")
public class BlacklistedToken {
    @Id
    private String token;
    private long expirationTime;

    // Constructors
    public BlacklistedToken() {
    }

    public BlacklistedToken(String token, long expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
