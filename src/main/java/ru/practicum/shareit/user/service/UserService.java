package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto getById(long id);

    User getEntityById(long id);

    UserDto create(UserDto userDto);

    UserDto update(long id, UserUpdateDto userUpdateDto);

    void delete(long id);
}
