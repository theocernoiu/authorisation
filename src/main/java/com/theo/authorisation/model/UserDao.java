package com.theo.authorisation.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "User")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Getter
    @Column(name = "LAST_NAME", nullable = false, unique = true)
    private String lastName;

    @Getter
    @Column(name = "FIRST_NAME", nullable = false, unique = true)
    private String firstName;

    @Getter
    @Column(name = "ADDRESS")
    private String address;

    @Getter
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Getter
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Getter
    @Column(name = "IS_ADMIN", nullable = false)
    private boolean isAdmin;

    @Column
    @Getter
    private String roles;
}
