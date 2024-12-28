package com.medix.medix.dtos.user;

import com.medix.medix.annotations.FieldMatch;
import com.medix.medix.annotations.Unique;
import com.medix.medix.entities.User;
import com.medix.medix.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword", message = "Паролите не съвпадат.")
@Unique(entityClass = User.class, fieldName = "username", message = "Потребителското име вече съществува.")
public class CreateBaseUserRequestDto {
    @NotBlank(message = "Потребителското име е задължително.")
    private String username;

    @NotBlank(message = "Паролата е задължителна.")
    private String password;

    @NotBlank(message = "Потвърждението на паролата е задължително.")
    private String confirmPassword;

    @NotNull(message = "Ролята е задължителна.")
    private Role role;

    @NotBlank(message = "Името е задължително.")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна.")
    private String lastName;

    public CreateBaseUserRequestDto() {
    }

    public CreateBaseUserRequestDto(Role role) {
        setRole(role);
    }
}