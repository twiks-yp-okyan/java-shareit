package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto getById(long id) {
        return repository.findById(id).map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Override
    public User getEntityById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if (findUserByEmail(user).isPresent()) {
            throw new ConflictDataException("Пользователь с таким email уже существует.");
        }
        user = repository.save(user);
        return UserMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto update(long id, UserUpdateDto userUpdateDto) {
        User currentUser = repository.findById(id)
                .map(user -> UserMapper.updateUserData(user, userUpdateDto))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
        if (findUserByEmail(currentUser).isPresent()) {
            throw new ConflictDataException("Пользователь с таким email уже существует.");
        }
        currentUser = repository.save(currentUser);
        return UserMapper.mapToDto(currentUser);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = repository.findById(id)
                        .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
        repository.delete(user);
    }

    private Optional<User> findUserByEmail(User user) {
        User currentUser = repository.findByEmail(user.getEmail());
        return currentUser != null && !currentUser.getId().equals(user.getId()) ? Optional.of(currentUser) : Optional.empty();
    }
}
