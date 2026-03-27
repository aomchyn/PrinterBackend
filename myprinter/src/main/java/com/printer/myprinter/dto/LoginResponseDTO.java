package com.printer.myprinter.dto;

import com.printer.myprinter.enums.Role;

public class LoginResponseDTO {
    private String token;
    private  Role role;

    public LoginResponseDTO(String token, Role role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
