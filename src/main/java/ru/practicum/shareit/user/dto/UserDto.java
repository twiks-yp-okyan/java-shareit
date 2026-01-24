package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    @NotNull(message = "Email не может быть пустым.")
    @Email(message = "Введенный email не является валидным.")
    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate registrationDate;
}
