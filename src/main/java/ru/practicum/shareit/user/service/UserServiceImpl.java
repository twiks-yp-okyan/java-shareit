package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Qualifier("inMemoryUserStorage")
    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto getById(long id) {
        return userStorage.getById(id).map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if (findUserByEmail(user).isPresent()) {
            throw new ConflictDataException("Пользователь с таким email уже существует.");
        }
        return UserMapper.mapToDto(userStorage.create(user));
    }

    @Override
    public UserDto update(long id, UserUpdateDto userUpdateDto) {
        User currentUser = userStorage.getById(id)
                .map(user -> UserMapper.updateUserData(user, userUpdateDto))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
        if (findUserByEmail(currentUser).isPresent()) {
            throw new ConflictDataException("Пользователь с таким email уже существует.");
        }
        currentUser = userStorage.update(currentUser);
        return UserMapper.mapToDto(currentUser);
    }

    @Override
    public void delete(long id) {
        userStorage.delete(id);
    }

    private Optional<User> findUserByEmail(User user) {
        for (User currentUser : userStorage.getUsers()) {
            if (user.getEmail().equals(currentUser.getEmail()) && !user.getId().equals(currentUser.getId())) {
                return Optional.of(currentUser);
            }
        }
        return Optional.empty();
    }
}
