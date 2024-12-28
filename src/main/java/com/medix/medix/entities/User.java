package com.medix.medix.entities;

import com.medix.medix.dtos.user.CreateBaseUserRequestDto;
import com.medix.medix.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity implements UserDetails, Serializable {
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Потребителското име е задължително")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Името е задължително")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Фамилията е задължителна")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "Паролата е задължителна")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Ролята е задължителна")
    private Role role;

    @Formula("CONCAT(first_name, ' ', last_name)")
    private String fullName;

    public User() {
        super();
    }

    public User(CreateBaseUserRequestDto baseUserRequestDto) {
        super();
        setFirstName(baseUserRequestDto.getFirstName());
        setLastName(baseUserRequestDto.getLastName());
        setUsername(baseUserRequestDto.getUsername());
        setPassword(baseUserRequestDto.getPassword());
        setRole(baseUserRequestDto.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(() -> role.name());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public boolean isDoctor() {
        return role == Role.ROLE_DOCTOR;
    }

    public boolean isPatient() {
        return role == Role.ROLE_PATIENT;
    }

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }
}
