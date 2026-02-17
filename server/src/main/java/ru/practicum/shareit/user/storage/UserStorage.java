package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    Optional<User> getById(long id);

    User create(User user);

    User update(User user);

    void delete(long id);
}
