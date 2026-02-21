package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
    private final UserRepository repository;
    private final UserService service;
    private final EntityManager em;

    private User basicUser;
    private UserDto basicUserDto;
    private UserUpdateDto basicUserUpdateDto;

    @BeforeEach
    public void createBasicUsers() {
        repository.deleteAll();

        basicUser = new User(1L, "Test", "test@gmail.com", LocalDate.now());

        basicUserDto = new UserDto();
        basicUserDto.setName("Test");
        basicUserDto.setEmail("test@gmail.com");

        basicUserUpdateDto = new UserUpdateDto();
        basicUserUpdateDto.setName("Test-update");
        basicUserUpdateDto.setEmail("test-update@gmail.com");
    }

    @Test
    public void shouldSaveNewUser() {
        UserDto savedUserDto = service.create(basicUserDto);
        User savedUser = UserMapper.mapToUser(savedUserDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User foundUser = query.setParameter("email", basicUserDto.getEmail())
                .getSingleResult();

        Assertions.assertEquals(savedUser.getName(), foundUser.getName());
        Assertions.assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    public void shouldGetAll3Users() {
        UserDto userForSavingDto = new UserDto();

        for (int i = 0; i < 3; i++) {
            String currentName = "Test " + i;
            String currentEmail = String.format("test%d@gmail.com", i);

            changeUserUpdateDtoData(userForSavingDto, currentName, currentEmail);
            service.create(userForSavingDto);
        }

        List<User> savedUsers = repository.findAll();

        Assertions.assertEquals(3, savedUsers.size());
    }

    @Test
    public void shouldGetUserById() {
        UserDto savedUser = service.create(basicUserDto);
        UserDto foundUser = service.getById(savedUser.getId());

        Assertions.assertEquals(savedUser.getId(), foundUser.getId());
        Assertions.assertEquals(savedUser.getEmail(), foundUser.getEmail());
        Assertions.assertEquals(savedUser.getRegistrationDate(), foundUser.getRegistrationDate());
    }

    @Test
    public void shouldThrowConflictDataExceptionAfterUpdateAttempt() {
        UserDto savedUser = service.create(basicUserDto);
        UserDto anotherSavedUser = new UserDto();
        anotherSavedUser.setName("Conflict");
        anotherSavedUser.setEmail("Conflict.email@gmail.com");
        anotherSavedUser = service.create(anotherSavedUser);

        UserUpdateDto userForUpdateDto = new UserUpdateDto();
        userForUpdateDto.setEmail(anotherSavedUser.getEmail());

        Assertions.assertThrows(ConflictDataException.class, () -> service.update(savedUser.getId(), userForUpdateDto));
    }

    @Test
    public void shouldDeleteUser() {
        UserDto savedUser = service.create(basicUserDto);
        service.delete(savedUser.getId());

        Assertions.assertThrows(NotFoundException.class, () -> service.getById(savedUser.getId()));
    }

    @Test
    public void shouldGetUsersPaginated() {
        UserDto dto = new UserDto();
        for (int i = 0; i < 3; i++) {
            changeUserUpdateDtoData(dto, "User " + i, "user" + i + "@gmail.com");
            service.create(dto);
        }

        Collection<UserDto> page1 = service.getUsers(0, 2);
        Collection<UserDto> pageAll = service.getUsers(0, 10);

        Assertions.assertEquals(2, page1.size());
        Assertions.assertEquals(3, pageAll.size());
    }

    @Test
    public void shouldGetByIdThrowWhenNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getById(999L));
    }

    @Test
    public void shouldGetEntityById() {
        UserDto saved = service.create(basicUserDto);

        User entity = service.getEntityById(saved.getId());

        Assertions.assertEquals(saved.getId(), entity.getId());
        Assertions.assertEquals("Test", entity.getName());
        Assertions.assertEquals("test@gmail.com", entity.getEmail());
    }

    @Test
    public void shouldGetEntityByIdThrowWhenNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getEntityById(999L));
    }

    @Test
    public void shouldCreateThrowWhenEmailExists() {
        service.create(basicUserDto);

        UserDto duplicateEmail = new UserDto();
        duplicateEmail.setName("Other");
        duplicateEmail.setEmail(basicUserDto.getEmail());

        Assertions.assertThrows(ConflictDataException.class, () -> service.create(duplicateEmail));
    }

    @Test
    public void shouldUpdateUser() {
        UserDto saved = service.create(basicUserDto);
        UserUpdateDto update = new UserUpdateDto();
        update.setName("Updated name");
        update.setEmail("updated@gmail.com");

        UserDto updated = service.update(saved.getId(), update);

        Assertions.assertEquals("Updated name", updated.getName());
        Assertions.assertEquals("updated@gmail.com", updated.getEmail());
    }

    @Test
    public void shouldUpdateThrowWhenUserNotFound() {
        Assertions.assertThrows(NotFoundException.class,
                () -> service.update(999L, basicUserUpdateDto));
    }

    @Test
    public void shouldDeleteThrowWhenUserNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.delete(999L));
    }

    private void changeUserUpdateDtoData(UserDto dto, String name, String email) {
        dto.setName(name);
        dto.setEmail(email);
    }
}
