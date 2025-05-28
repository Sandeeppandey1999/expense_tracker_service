package com.expensetracker.dto;

public class AuthRequest {
    private String loginField; // username, email, or mobile
    private String password;

    public String getLoginField() {
        return loginField;
    }
    public void setLoginField(String loginField) {
        this.loginField = loginField;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
