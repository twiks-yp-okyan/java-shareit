package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static User basicUser;
    private static UserDto basicUserDto;
    private static UserUpdateDto basicUserUpdateDto;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeAll
    public static void createBasicUsers() {
        basicUser = new User(1L, "Test", "test@gmail.com", LocalDate.now());

        basicUserDto = new UserDto();
        basicUserDto.setName("Test");
        basicUserDto.setEmail("test@gmail.com");

        basicUserUpdateDto = new UserUpdateDto();
        basicUserUpdateDto.setName("Test update");
        basicUserUpdateDto.setEmail("test@gmail.com");
    }

    @Test
    public void testSaveUser() {
        Mockito
                .when(userRepository.save(any()))
                        .thenReturn(basicUser);

        UserDto savedUserDto = userService.create(basicUserDto);
        User savedUser = UserMapper.mapToUser(savedUserDto);

        Assertions.assertEquals(savedUser.getName(), basicUser.getName());
        Assertions.assertEquals(savedUser.getEmail(), basicUser.getEmail());
        Assertions.assertEquals(savedUser.getRegistrationDate(), basicUser.getRegistrationDate());
    }

    @Test
    public void shouldFindUserById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(basicUser));

        UserDto foundUserDto = userService.getById(basicUser.getId());
        User foundUser = UserMapper.mapToUser(foundUserDto);

        assertThat(foundUser.getEmail(), equalTo(basicUser.getEmail()));
    }

    @Test
    public void shouldThrowConflictDataWhenUpdate() {
        User existedUser = new User(2L, "Test 2", "test@gmail.com", LocalDate.now());

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(basicUser));

        Mockito
                .when(userRepository.findByEmail(anyString()))
                .thenReturn(existedUser);

        Assertions.assertThrows(ConflictDataException.class, () -> userService.update(basicUser.getId(), basicUserUpdateDto));
    }

    @Test
    public void shouldDeleteUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(basicUser));

        userService.delete(basicUser.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .delete(basicUser);
    }
}
