package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        LocalDate registrationDate = dto.getRegistrationDate() == null ? LocalDate.now() : dto.getRegistrationDate();
        user.setRegistrationDate(registrationDate);
        return user;
    }

    public static UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }

    public static User updateUserData(User currentUser, UserUpdateDto updatedUser) {
        if (updatedUser.hasName()) {
            currentUser.setName(updatedUser.getName());
        }
        if (updatedUser.hasEmail()) {
            currentUser.setEmail(updatedUser.getEmail());
        }
        return currentUser;
    }
}
