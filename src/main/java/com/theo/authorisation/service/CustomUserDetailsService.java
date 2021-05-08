package com.theo.authorisation.service;

import com.theo.authorisation.model.CustomUserDetails;
import com.theo.authorisation.model.UserDao;
import com.theo.authorisation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Qualifier("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserDao> user = repository.findByEmail(email);
        log.debug("user: {}", user);
        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Nothing found for + " + email));
    }
}
