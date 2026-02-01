package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    Long id;
    String name;
    String email;
    @Builder.Default
    LocalDate registrationDate = LocalDate.now();
}
