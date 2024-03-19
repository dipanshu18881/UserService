package com.enterpriseproject.userservice.Services;

import com.enterpriseproject.userservice.DTOs.SendEmailEventDto;
import com.enterpriseproject.userservice.Models.Token;
import com.enterpriseproject.userservice.Models.User;
import com.enterpriseproject.userservice.Repositories.TokenRepository;
import com.enterpriseproject.userservice.Repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class SelfUserService implements UserService {

    // Autowired repositories and password encoder
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;


    //  Constructor
    public SelfUserService(UserRepository userRepository,
                           TokenRepository tokenRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //  User sign up method
    @Override
    public User signUp(String fullName, String email, String password) {
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        SendEmailEventDto sendEmailEvent = new SendEmailEventDto();
        sendEmailEvent.setTo(email);
        sendEmailEvent.setFrom("dev.env.dipanshu@gmail.com");
        sendEmailEvent.setSubject("Welcome to Second Life!");
        sendEmailEvent.setBody("Welcome to Second Life!" +
                "This is a test mail."
        );

        try {
            kafkaTemplate.send(
                    "sendEmail",
                    objectMapper.writeValueAsString(sendEmailEvent)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }

    //  User login method
    @Override
    public Token login(String email, String password) {

        //  Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        //  If user does not exist, throw exception
        if(userOptional.isEmpty())
            throw new RuntimeException("User does not exist");

        // If password does not match, throw exception
        if(!bCryptPasswordEncoder.matches(
                password, userOptional.get()
                        .getHashedPassword()))
            throw new RuntimeException("Password did not match");


        return tokenRepository.save(getToken(userOptional.get()));
    }

    //  Get token method
    private static Token getToken(User user) {
        // Get today's date and 30 days later date
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);

        // Convert LocalDate to Date
        Date expiryDate = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setUser(user);
        token.setExpiryAt(expiryDate);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        return token;
    }

    //  User logout method
    @Override
    public void logout(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeleted(token, false);

        if(tokenOptional.isEmpty()) {
//            throw new Exception("Token not present or already expired");
            return;
        }

        Token tkn = tokenOptional.get();
        tkn.setDeleted(true);
        tokenRepository.save(tkn);
    }

    @Override
    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeletedEqualsAndExpiryAtGreaterThan(token, false, new Date());

        if(tokenOptional.isEmpty()) {
            return null;
        }

        return tokenOptional.get().getUser();
    }
}
