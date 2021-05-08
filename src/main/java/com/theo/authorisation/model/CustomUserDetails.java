package com.theo.authorisation.model;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter @Setter
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(UserDao user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Splitter.on(",")
                .splitToStream(Optional.ofNullable(user.getRoles()).orElse(Strings.EMPTY))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
