package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String name;
    private String email;

    public Boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public Boolean hasName() {
        return !(name == null || name.isBlank());
    }

}
