package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void testSaveUser() {
        User user = createNewUser();
        UserDto userDto = createUserDto();

        Mockito
                .when(userRepository.save(any()))
                        .thenReturn(user);

        UserDto savedUserDto = userService.create(userDto);
        User savedUser = UserMapper.mapToUser(savedUserDto);

        Assertions.assertEquals(savedUser.getName(), user.getName());
        Assertions.assertEquals(savedUser.getEmail(), user.getEmail());
        Assertions.assertEquals(savedUser.getRegistrationDate(), user.getRegistrationDate());
    }

    private User createNewUser() {
        return new User(1L, "Test", "test@gmail.com", LocalDate.now());
    }

    private UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Test");
        dto.setEmail("test@gmail.com");
        return dto;
    }
}
