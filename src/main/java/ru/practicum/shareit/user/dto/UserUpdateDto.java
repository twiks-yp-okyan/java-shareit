package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String name;
    @Email(message = "Введенный email не является валидным.")
    private String email;

    public Boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public Boolean hasName() {
        return !(name == null || name.isBlank());
    }

}
