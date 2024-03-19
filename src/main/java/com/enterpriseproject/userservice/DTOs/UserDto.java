package com.enterpriseproject.userservice.DTOs;

import com.enterpriseproject.userservice.Models.Role;
import com.enterpriseproject.userservice.Models.User;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    @ManyToMany
    private List<Role> roles;
    private boolean isEmailVerified;

public static UserDto from(User user) {
        if (user == null) return null;

        UserDto userDto = new UserDto();
        userDto.email = user.getEmail();
        userDto.name = user.getName();
        userDto.roles = user.getRoles();
        userDto.isEmailVerified = user.isEmailVerified();

        return userDto;
    }
}
