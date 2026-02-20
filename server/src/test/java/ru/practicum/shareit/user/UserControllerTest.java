package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserDto userDto;
    UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("test@gmail.com");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Test-update");
        userUpdateDto.setEmail("test-update@gmail.com");
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setEmail("test@gmail.com");
        responseDto.setName("Test");

        Mockito
                .when(userService.create(any()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setEmail("test@gmail.com");
        responseDto.setName("Test");

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/users/1")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    public void shouldUpdateUserEmail() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setEmail("test@gmail.com");
        responseDto.setName("Test");

        UserDto responseUpdDto = new UserDto();
        responseUpdDto.setId(1L);
        responseUpdDto.setEmail("test-update@gmail.com");
        responseUpdDto.setName("Test-update");

        Mockito
                .when(userService.create(any()))
                .thenReturn(responseDto);

        Mockito
                .when(userService.update(anyLong(), any()))
                .thenReturn(responseUpdDto);

        mockMvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(userUpdateDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test-update@gmail.com"));
    }

}
