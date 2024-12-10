package vttp.ssf_prac4.model;

import jakarta.validation.constraints.Size;

public class LoginUser {
    
    @Size(min = 2, message = "Username must have at least 2 characters.")
    private String username;

    @Size(min = 2, message = "Password must have at least 2 characters.")
    private String password;

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
    @Override
    public String toString() {
        return "LoginUser [username=" + username + ", password=" + password + "]";
    }
    
}
