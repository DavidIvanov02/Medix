package com.medix.medix.repositories;

import com.medix.medix.entities.Doctor;
import com.medix.medix.entities.Patient;
import com.medix.medix.entities.User;
import com.medix.medix.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    private User adminUser;
    private Patient patientUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword("adminpassword");
        adminUser.setRole(Role.ROLE_ADMIN);
        testEntityManager.persistAndFlush(adminUser);

        Doctor doctorUser = new Doctor();
        doctorUser.setUsername("doctor");
        doctorUser.setFirstName("Doctor");
        doctorUser.setLastName("User");
        doctorUser.setPassword("doctorpassword");
        doctorUser.setRole(Role.ROLE_DOCTOR);
        testEntityManager.persistAndFlush(doctorUser);

        patientUser = new Patient();
        patientUser.setUsername("patient");
        patientUser.setFirstName("Patient");
        patientUser.setLastName("User");
        patientUser.setPassword("patientpassword");
        patientUser.setRole(Role.ROLE_PATIENT);
        patientUser.setEgn("1234567890");
        patientUser.setGeneralPractitioner(doctorUser);
        testEntityManager.persistAndFlush(patientUser);
    }

    @Test
    void findByUsernameTest() {
        User retrievedUser = userRepository.findByUsername("admin");

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("admin");
        assertThat(retrievedUser.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void findAllAdminUsersTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> adminUsers = userRepository.findAllAdminUsers(pageable);

        assertThat(adminUsers.getTotalElements()).isEqualTo(1);
        assertThat(adminUsers.getContent().get(0).getUsername()).isEqualTo("admin");
        assertThat(adminUsers.getContent().get(0).getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void findAdminByIdTest() {
        User retrievedAdmin = userRepository.findAdminById(adminUser.getId());

        assertThat(retrievedAdmin).isNotNull();
        assertThat(retrievedAdmin.getUsername()).isEqualTo("admin");
        assertThat(retrievedAdmin.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void findAllByRoleIsInTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = userRepository.findAllByRoleIsIn(pageable, Role.ROLE_ADMIN, Role.ROLE_PATIENT);

        assertThat(users.getTotalElements()).isEqualTo(2);
        assertThat(users.getContent()).extracting("role").containsExactlyInAnyOrder(Role.ROLE_ADMIN, Role.ROLE_PATIENT);
    }
}
