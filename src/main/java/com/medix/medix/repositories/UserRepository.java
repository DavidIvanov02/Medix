package com.medix.medix.repositories;

import com.medix.medix.entities.User;
import com.medix.medix.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_ADMIN'")
    Page<User> findAllAdminUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = 'ROLE_ADMIN'")
    User findAdminById(Long id);

    Page<User> findAllByRoleIsIn(Pageable pageable, Role... roles);
}