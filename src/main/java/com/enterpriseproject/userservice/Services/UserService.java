package com.enterpriseproject.userservice.Services;

import com.enterpriseproject.userservice.Models.Token;
import com.enterpriseproject.userservice.Models.User;

public interface UserService {
    public User signUp(String fullName, String email, String password);

    public Token login(String email, String password);

    public void logout(String token);
    public User validateToken(String token);

}
