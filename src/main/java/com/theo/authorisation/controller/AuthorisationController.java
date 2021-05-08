package com.theo.authorisation.controller;

import com.theo.authorisation.model.AuthenticationRequest;
import com.theo.authorisation.model.AuthenticationResponse;
import com.theo.authorisation.model.UserDao;
import com.theo.authorisation.repository.UserRepository;
import com.theo.authorisation.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AuthorisationController {

    @Autowired
    UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("customUserDetailsService")
    @Autowired
    private UserDetailsService userDetailService;

    @Autowired
    private JwtUtil tokenUtil;

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

    @RequestMapping(value = {"/authenticate"}, method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Error occurred while authenticating", e);
            throw new Exception("Incorrect user/pass", e);
        }
        UserDetails ud = userDetailService.loadUserByUsername(request.getUsername());

        log.debug("Fetched user from db: {}", ud);

        log.debug("Generating token!");
        final String jwt = tokenUtil.generateToken(ud);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
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
