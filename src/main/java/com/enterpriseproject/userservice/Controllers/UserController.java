package com.enterpriseproject.userservice.Controllers;

import com.enterpriseproject.userservice.DTOs.LoginRequestDto;
import com.enterpriseproject.userservice.DTOs.LogoutRequestDto;
import com.enterpriseproject.userservice.DTOs.SignupRequestDto;
import com.enterpriseproject.userservice.DTOs.UserDto;
import com.enterpriseproject.userservice.Models.Token;
import com.enterpriseproject.userservice.Models.User;
import com.enterpriseproject.userservice.Services.SelfUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final SelfUserService selfUserService;

    //  Constructor
    @Autowired
    public UserController(SelfUserService selfUserService) {
        this.selfUserService = selfUserService;
    }


    //  Post mapping for user to sign up
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto signupRequest) {
        return new ResponseEntity<>(
                UserDto.from(selfUserService.signUp(
                        signupRequest.getName(),
                        signupRequest.getEmail(),
                        signupRequest.getPassword())),
                HttpStatus.CREATED);
    }


    //  Post mapping for user to login
    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequestDto request) {
        return new ResponseEntity<>(
                selfUserService.login(request.getEmail(), request.getPassword()),
                HttpStatus.OK
        );
    }


    // Post mapping for user to logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        selfUserService.logout(request.getToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //  Post mapping for user to validate token
    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") @NonNull String token) {
        return UserDto.from(selfUserService.validateToken(token));
    }
}
