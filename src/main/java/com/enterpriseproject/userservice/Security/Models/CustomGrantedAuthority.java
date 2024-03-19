package com.enterpriseproject.userservice.Security.Models;

import com.enterpriseproject.userservice.Models.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

@JsonDeserialize
public class CustomGrantedAuthority implements GrantedAuthority {
//    private Role role;
    private String authority;

    public CustomGrantedAuthority() {}
    public CustomGrantedAuthority(Role role) {
//        this.role = role;
        this.authority = role.getName();
    }

    @Override
    public String getAuthority() {
//        return role.getName();
        return authority;
    }
}
