package com.theo.authorisation.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(UserDao user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Lists.newArrayList();
//        this.authorities = Arrays.stream(Optional.ofNullable(user.getRoles()).orElseGet( () -> Strings.EMPTY).split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
        log.debug("Done creating: {}", this);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
