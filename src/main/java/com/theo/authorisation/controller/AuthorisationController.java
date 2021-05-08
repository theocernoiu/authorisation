package com.theo.authorisation.controller;

import com.theo.authorisation.model.UserDao;
import com.theo.authorisation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class AuthorisationController {

    @Autowired
    UserRepository repository;

    @PostMapping({"/register"})
    public ResponseEntity<UserDao> registerNewUser(@RequestBody UserDao newUser) {
        try {
            UserDao user = repository.save(UserDao.builder()
                    .email(newUser.getEmail())
                    .password(newUser.getPassword())
                    .lastName(newUser.getLastName())
                    .firstName(newUser.getFirstName())
                    .address(newUser.getAddress())
                    .isAdmin(newUser.getIsAdmin())
                    .build());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Caught Exception: {}", ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/users"})
    public ResponseEntity<List<UserDao>> getAllUsers() {
        log.debug("Get all users");
        try {
            return new ResponseEntity<>(repository.findAll(), HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Caught Exception: {}", ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
