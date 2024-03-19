package com.enterpriseproject.userservice.Security.Services;

import com.enterpriseproject.userservice.Models.User;
import com.enterpriseproject.userservice.Repositories.UserRepository;
import com.enterpriseproject.userservice.Security.Models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        if(userOptional.isEmpty()) throw new UsernameNotFoundException("User by email: " + username + " not found");

        return new CustomUserDetails(userOptional.get());
    }
}
