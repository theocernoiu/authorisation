package com.theo.authorisation.controller;

import com.theo.authorisation.model.AuthenticationRequest;
import com.theo.authorisation.model.AuthenticationResponse;
import com.theo.authorisation.model.UserDao;
import com.theo.authorisation.repository.UserRepository;
import com.theo.authorisation.util.JwtUtil;
import com.theo.authorisation.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private TokenProvider tokenProvider;
    //private JwtUtil tokenUtil;

    @CrossOrigin
    @PostMapping({"/register"})
    public ResponseEntity<UserDao> registerNewUserd(@RequestBody UserDao newUser) {
        try {
            UserDao user = repository.save(UserDao.builder()
                    .email(newUser.getEmail())
                    .password(newUser.getPassword())
                    .lastName(newUser.getLastName())
                    .firstName(newUser.getFirstName())
                    .address(newUser.getAddress())
                    .isAdmin(newUser.isAdmin())
                    .build());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Caught Exception: {}", ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @RequestMapping(value = {"/authenticate"}, method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Error occurred while authenticating", e);
            throw new Exception("Incorrect user/pass", e);
        }
        UserDetails ud = userDetailService.loadUserByUsername(request.getUsername());

        log.debug("Fetched user from db: {}", ud);

        log.debug("Generating token!");
        final String jwt = tokenProvider.createToken(auth);

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

    @GetMapping({"/users/current"})
    public ResponseEntity<UserDao> getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Currently logged in: {}", auth.getName());

        UsernamePasswordAuthenticationToken tok = (UsernamePasswordAuthenticationToken) auth;
        UserDetails ud = (UserDetails) tok.getPrincipal();

        log.debug("UserDetails: {}", ud);


        return repository.findByEmail(ud.getUsername()).map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
