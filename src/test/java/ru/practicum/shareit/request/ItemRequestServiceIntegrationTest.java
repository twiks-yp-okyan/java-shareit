package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService service;
    private final EntityManager em;

    @Test
    public void shouldSaveNewItemRequest() {
        UserDto newUserRequest = createUserDto();
        UserDto newUserDto = userService.create(newUserRequest);
        ItemRequestDto itemRequestDto = createNewRequestDto();
        ItemRequestDto savedRequest = service.create(newUserDto.getId(), itemRequestDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
        User user = query.setParameter("name", newUserRequest.getName())
                .getSingleResult();

        Assertions.assertEquals(user.getName(), newUserRequest.getName());

        Assertions.assertNotNull(savedRequest.getId());
        Assertions.assertEquals("item request description", savedRequest.getDescription());
        Assertions.assertEquals(newUserDto.getId(), savedRequest.getUser().getId());
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

    private ItemRequestDto createNewRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("item request description");
        return dto;
    }

}
