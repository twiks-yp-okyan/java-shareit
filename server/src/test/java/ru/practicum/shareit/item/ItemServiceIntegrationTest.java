package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ItemServiceIntegrationTest {
    private final ItemRepository repository;
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final EntityManager entityManager;
    private final Clock clock;

    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private UserDto userDto;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void setUp() {
        repository.deleteAll();

        itemDto = new ItemDto();
        itemDto.setName("Test");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);

        itemUpdateDto = new ItemUpdateDto("Test update", "Test update description", true);

        userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("test@gmail.com");
        userDto = userService.create(userDto);
    }

    @Test
    public void shouldCreateNewItem() {
        ItemDto createdItemDto = service.create(userDto.getId(), itemDto);
        Item createdItem = ItemMapper.mapToItem(createdItemDto);
        createdItem.setOwner(UserMapper.mapToUser(userDto));

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item foundItem = query.setParameter("name", createdItem.getName()).getSingleResult();

        Assertions.assertEquals(createdItem.getId(), foundItem.getId());
        Assertions.assertEquals(createdItem.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(createdItem.getOwner().getId(), foundItem.getOwner().getId());
    }

    @Test
    public void shouldGetUserItems() {
        ItemDto dto = new ItemDto();
        for (int i = 0; i < 3; i++) {
            String name = "Test" + i;
            String description = "Test description " + i;
            Boolean available = i % 2 == 0;
            changeItemData(dto, name, description, available);
            service.create(userDto.getId(), dto);
        }

        List<ItemWithBookingDatesAndCommentsDto> userItems = service.getUserItems(userDto.getId());

        Assertions.assertEquals(3, userItems.size());
    }

    @Test
    public void shouldThrowUserNotOwner() {
        ItemDto createdItemDto = service.create(userDto.getId(), itemDto);

        Assertions.assertThrows(
                UserNotOwnerException.class,
                () -> service.update(userDto.getId() + 1, createdItemDto.getId(), itemUpdateDto)
        );
    }

    @Test
    public void shouldFindItemBySearchText() {
        ItemDto createdItemDto = service.create(userDto.getId(), itemDto);
        List<ItemDto> foundItems = service.getItemsBySearchText("est");

        Assertions.assertEquals(1, foundItems.size());
        Assertions.assertEquals("Test", foundItems.getFirst().getName());
    }

    @Test
    public void shouldSaveNewComment() throws InterruptedException {
        UserDto userDto2 = new UserDto();
        userDto2.setName("Test2");
        userDto2.setEmail("testxyz@gmail.com");
        userDto2 = userService.create(userDto2);

        ItemDto createdItemDto = service.create(userDto.getId(), itemDto);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                createdItemDto.getId(),
                LocalDateTime.now(clock).format(formatter),
                LocalDateTime.now(clock).plusSeconds(1).format(formatter)
        );
        BookingDto createdBookingDto = bookingService.create(bookingRequestDto, userDto2.getId());
        bookingService.reviewBooking(createdItemDto.getOwnerId(), createdBookingDto.getId(), true);
        Thread.sleep(1500);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment For Test Item");
        CommentDto savedComment = service.addNewComment(userDto2.getId(), createdItemDto.getId(), commentDto);

        ItemWithBookingDatesAndCommentsDto itemWithComment = service.getItemById(createdItemDto.getId());

        Assertions.assertEquals(1, itemWithComment.getComments().size());
    }

    @Test
    public void shouldReturnItemsByRequestId() {
        UserDto userDto2 = new UserDto();
        userDto2.setName("Test2");
        userDto2.setEmail("testxyz@gmail.com");
        userDto2 = userService.create(userDto2);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("bla bla bla");
        ItemRequestDto createdRequest = itemRequestService.create(userDto2.getId(), itemRequestDto);

        itemDto.setRequestId(createdRequest.getId());

        ItemDto createdItem = service.create(userDto.getId(), itemDto);

        List<ItemDto> itemsByRequestId = service.getItemsByRequestIds(List.of(createdRequest.getId()));

        Assertions.assertEquals(1, itemsByRequestId.size());
        Assertions.assertEquals("Test", itemsByRequestId.getFirst().getName());
    }

    private void changeItemData(ItemDto itemDto, String name, String description, Boolean available) {
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
    }
}
