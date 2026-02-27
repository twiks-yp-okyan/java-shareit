package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private UserDto owner;
    private UserDto booker;
    private UserDto otherUser;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = userService.create(createUserDto("Owner", "owner@test.com"));
        booker = userService.create(createUserDto("Booker", "booker@test.com"));
        otherUser = userService.create(createUserDto("Other", "other@test.com"));

        itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto = itemService.create(owner.getId(), itemDto);
    }

    @Test
    void create_shouldSaveNewBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto(
                itemDto.getId(),
                start.format(FORMATTER),
                end.format(FORMATTER)
        );

        BookingDto created = bookingService.create(request, booker.getId());

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(BookingStatus.WAITING, created.getStatus());
        Assertions.assertEquals(booker.getId(), created.getBooker().getId());
        Assertions.assertEquals(itemDto.getId(), created.getItem().getId());
    }

    @Test
    void create_shouldThrowWhenStartEqualsEnd() {
        LocalDateTime same = LocalDateTime.now().plusDays(1);
        BookingRequestDto request = new BookingRequestDto(
                itemDto.getId(),
                same.format(FORMATTER),
                same.format(FORMATTER)
        );

        Assertions.assertThrows(BookingConflictException.class,
                () -> bookingService.create(request, booker.getId()));
    }

    @Test
    void create_shouldThrowWhenItemNotAvailable() {
        itemService.update(owner.getId(), itemDto.getId(), ItemUpdateDto.builder().available(false).build());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto request = new BookingRequestDto(
                itemDto.getId(),
                start.format(FORMATTER),
                end.format(FORMATTER)
        );

        Assertions.assertThrows(BookingConflictException.class,
                () -> bookingService.create(request, booker.getId()));
    }

    @Test
    void reviewBooking_shouldApprove() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        BookingDto reviewed = bookingService.reviewBooking(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(BookingStatus.APPROVED, reviewed.getStatus());
    }

    @Test
    void reviewBooking_shouldReject() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        BookingDto reviewed = bookingService.reviewBooking(owner.getId(), booking.getId(), false);

        Assertions.assertEquals(BookingStatus.REJECTED, reviewed.getStatus());
    }

    @Test
    void reviewBooking_shouldThrowWhenNotOwner() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);

        Assertions.assertThrows(UserNotOwnerException.class,
                () -> bookingService.reviewBooking(booker.getId(), booking.getId(), true));
    }

    @Test
    void getById_shouldReturnWhenBooker() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        BookingDto found = bookingService.getById(booker.getId(), booking.getId());

        Assertions.assertEquals(booking.getId(), found.getId());
    }

    @Test
    void getById_shouldReturnWhenOwner() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        BookingDto found = bookingService.getById(owner.getId(), booking.getId());

        Assertions.assertEquals(booking.getId(), found.getId());
    }

    @Test
    void getById_shouldThrowWhenNotBookerNorOwner() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);

        Assertions.assertThrows(BookingConflictException.class,
                () -> bookingService.getById(otherUser.getId(), booking.getId()));
    }

    @Test
    void getById_shouldThrowWhenBookingNotFound() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getById(owner.getId(), 999L));
    }

    @Test
    void getCurrentUserBookingsByState_shouldReturnAll() {
        createBooking(booker.getId(), 1, 2);
        createBooking(booker.getId(), 3, 4);

        List<BookingDto> all = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.ALL);

        Assertions.assertEquals(2, all.size());
    }

    @Test
    void getCurrentUserBookingsByState_shouldFilterByFuture() {
        createBooking(booker.getId(), 1, 2);
        createBooking(booker.getId(), 10, 11);

        List<BookingDto> future = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.FUTURE);

        Assertions.assertEquals(2, future.size());
    }

    @Test
    void getCurrentUserBookingsByState_shouldFilterByPast() {
        createBooking(booker.getId(), -2, -1);

        List<BookingDto> past = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.PAST);

        Assertions.assertEquals(1, past.size());
    }

    @Test
    void getCurrentUserBookingsByState_shouldFilterByCurrent() {
        createBooking(booker.getId(), -1, 1);

        List<BookingDto> current = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.CURRENT);

        Assertions.assertEquals(1, current.size());
    }

    @Test
    void getCurrentUserBookingsByState_shouldFilterByWaiting() {
        createBooking(booker.getId(), 1, 2);

        List<BookingDto> waiting = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.WAITING);

        Assertions.assertEquals(1, waiting.size());
        Assertions.assertEquals(BookingStatus.WAITING, waiting.get(0).getStatus());
    }

    @Test
    void getCurrentUserBookingsByState_shouldFilterByRejected() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        bookingService.reviewBooking(owner.getId(), booking.getId(), false);

        List<BookingDto> rejected = bookingService.getCurrentUserBookingsByState(booker.getId(), BookingState.REJECTED);

        Assertions.assertEquals(1, rejected.size());
        Assertions.assertEquals(BookingStatus.REJECTED, rejected.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsByState_shouldReturnAll() {
        createBooking(booker.getId(), 1, 2);
        createBooking(otherUser.getId(), 3, 4);

        List<BookingDto> all = bookingService.getOwnerBookingsByState(owner.getId(), BookingState.ALL);

        Assertions.assertEquals(2, all.size());
    }

    @Test
    void getOwnerBookingsByState_shouldFilterByFuture() {
        createBooking(booker.getId(), 1, 2);

        List<BookingDto> future = bookingService.getOwnerBookingsByState(owner.getId(), BookingState.FUTURE);

        Assertions.assertEquals(1, future.size());
    }

    @Test
    void getOwnerBookingsByState_shouldFilterByRejected() {
        BookingDto booking = createBooking(booker.getId(), 1, 2);
        bookingService.reviewBooking(owner.getId(), booking.getId(), false);

        List<BookingDto> rejected = bookingService.getOwnerBookingsByState(owner.getId(), BookingState.REJECTED);

        Assertions.assertEquals(1, rejected.size());
        Assertions.assertEquals(BookingStatus.REJECTED, rejected.get(0).getStatus());
    }

    private BookingDto createBooking(Long bookerId, int startDaysFromNow, int endDaysFromNow) {
        LocalDateTime start = LocalDateTime.now().plusDays(startDaysFromNow);
        LocalDateTime end = LocalDateTime.now().plusDays(endDaysFromNow);
        BookingRequestDto request = new BookingRequestDto(
                itemDto.getId(),
                start.format(FORMATTER),
                end.format(FORMATTER)
        );
        return bookingService.create(request, bookerId);
    }

    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}
