package com.medix.medix.dtos.user;

import com.medix.medix.annotations.FieldMatch;
import com.medix.medix.annotations.Unique;
import com.medix.medix.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Unique(entityClass = User.class, fieldName = "username", message = "Потребителското име вече съществува.")
@FieldMatch(first = "password", second = "confirmPassword", message = "Паролите не съвпадат.")
public class EditBaseUserRequestDto {
    private Long id;

    @NotBlank(message = "Потребителското име е задължително.")
    private String username;

    private String password;

    private String confirmPassword;

    @NotBlank(message = "Името е задължително.")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна.")
    private String lastName;

    public EditBaseUserRequestDto() {}

    public EditBaseUserRequestDto(User user)
    {
        setUsername(user.getUsername());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setId(user.getId());
    }
}