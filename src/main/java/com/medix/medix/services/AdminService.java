package com.medix.medix.services;

import com.medix.medix.dtos.user.CreateBaseUserRequestDto;
import com.medix.medix.dtos.user.EditBaseUserRequestDto;
import com.medix.medix.entities.User;
import com.medix.medix.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAllAdminUsers(pageable);
    }

    public User create(CreateBaseUserRequestDto baseUserRequestDto, BindingResult bindingResult) {
        if (baseUserRequestDto == null) {
            bindingResult.rejectValue("username", "error.user", "Грешка при създаване на управлител.");
            return null;
        }

        baseUserRequestDto.setPassword(passwordEncoder.encode(baseUserRequestDto.getPassword()));

        User doctor = new User(baseUserRequestDto);

        try {
            return userRepository.save(doctor);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("username", "error.user", "Грешка при създаване на управлител.");
            return null;
        }
    }

    public User update(Long id, EditBaseUserRequestDto editBaseUserRequestDto, BindingResult bindingResult) {
        if (editBaseUserRequestDto == null) {
            bindingResult.rejectValue("username", "error.user", "Грешка при редакция на потребител");
            return null;
        }

        User user;
        try {
            user = findById(id);
        } catch (Exception e) {
            bindingResult.rejectValue("username", "error.user", "Администраторът не съществува");
            return null;
        }

        if (editBaseUserRequestDto.getPassword() != null && !editBaseUserRequestDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(editBaseUserRequestDto.getPassword()));
        }

        user.setFirstName(editBaseUserRequestDto.getFirstName());
        user.setLastName(editBaseUserRequestDto.getLastName());
        user.setUsername(editBaseUserRequestDto.getUsername());

        try {
            User admin = userRepository.save(user);

            if (admin.getId().equals(UserServiceImpl.getCurrentUser().getId())) {
                UserServiceImpl.setCurrentUser(admin);
            }

            return admin;
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("username", "error.user", "Грешка при редактиране на управлител.");

            return null;
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
