package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserService userService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    public void shouldGetItemById() {
        Long itemId = 1L;
        Long userId = 1L;
        Item newItem = createNewItem();
        ItemDto itemDto = createItemDto();

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(newItem);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(newItem));

        ItemDto savedItemDto = itemService.create(userId, itemDto);

        ItemWithBookingDatesAndCommentsDto foundItem = itemService.getItemById(itemId);

        Assertions.assertEquals(savedItemDto.getOwnerId(), foundItem.getOwnerId());

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(any());
    }

    private Item createNewItem() {
        User owner = createNewUser();
        return new Item(1L, owner, "item name", "item description", true, null);
    }

    private ItemDto createItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("item name");
        dto.setDescription("item description");
        dto.setAvailable(true);
        return dto;
    }

    private User createNewUser() {
        return new User(1L, "Test", "test@gmail.com", LocalDate.now());
    }
}
