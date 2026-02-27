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
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.util.List;

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

    @Test
    void getAllItemRequests_shouldReturnPaginatedSortedByCreatedDesc() {
        UserDto user1 = userService.create(createUserDto());
        UserDto user2 = userService.create(createUserDto("Other", "other@gmail.com"));

        ItemRequestDto req1 = createNewRequestDto();
        req1.setDescription("first request");
        ItemRequestDto req2 = createNewRequestDto();
        req2.setDescription("second request");
        service.create(user1.getId(), req1);
        service.create(user1.getId(), req2);

        List<ItemRequestDto> all = service.getAllItemRequests(user2.getId(), 0, 10);

        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(
                all.get(0).getCreated().isAfter(all.get(1).getCreated()) ||
                        all.get(0).getCreated().equals(all.get(1).getCreated()));
    }

    @Test
    void getUserRequests_shouldReturnOnlyUserRequestsWithItems() {
        UserDto requester = userService.create(createUserDto());
        UserDto owner = userService.create(createUserDto("Owner", "owner@gmail.com"));

        ItemRequestDto requestDto = createNewRequestDto();
        requestDto.setDescription("need a drill");
        ItemRequestDto createdRequest = service.create(requester.getId(), requestDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Good drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(createdRequest.getId());
        itemService.create(owner.getId(), itemDto);

        List<ItemRequestWithResponseDto> userRequests = service.getUserRequests(requester.getId());

        Assertions.assertEquals(1, userRequests.size());
        Assertions.assertEquals(createdRequest.getId(), userRequests.get(0).getId());
        Assertions.assertEquals("need a drill", userRequests.get(0).getDescription());
        Assertions.assertNotNull(userRequests.get(0).getItems());
        Assertions.assertEquals(1, userRequests.get(0).getItems().size());
        Assertions.assertEquals("Drill", userRequests.get(0).getItems().get(0).getName());
    }

    @Test
    void getByIdWithResponse_shouldReturnRequestWithItems() {
        UserDto requester = userService.create(createUserDto());
        UserDto owner = userService.create(createUserDto("Owner", "owner@gmail.com"));

        ItemRequestDto requestDto = createNewRequestDto();
        ItemRequestDto createdRequest = service.create(requester.getId(), requestDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item for request");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(createdRequest.getId());
        itemService.create(owner.getId(), itemDto);

        ItemRequestWithResponseDto result = service.getByIdWithResponse(requester.getId(), createdRequest.getId());

        Assertions.assertEquals(createdRequest.getId(), result.getId());
        Assertions.assertEquals(createdRequest.getDescription(), result.getDescription());
        Assertions.assertNotNull(result.getItems());
        Assertions.assertEquals(1, result.getItems().size());
        Assertions.assertEquals("Item for request", result.getItems().get(0).getName());
    }

    @Test
    void getEntityById_shouldReturnRequestWhenExists() {
        UserDto user = userService.create(createUserDto());
        ItemRequestDto created = service.create(user.getId(), createNewRequestDto());

        ItemRequest entity = service.getEntityById(created.getId());

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(created.getId(), entity.getId());
        Assertions.assertEquals(created.getDescription(), entity.getDescription());
    }

    @Test
    void getEntityById_shouldThrowWhenNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getEntityById(999L));
    }

    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}
