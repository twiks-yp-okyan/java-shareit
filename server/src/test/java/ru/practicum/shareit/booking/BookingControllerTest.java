package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 100L;

    // ─────────────────────────────────────────────────────────────
    // POST /bookings — создание бронирования
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /bookings — успешное создание")
    void shouldCreateBooking() throws Exception {
        User user = new User(1L, "Test", "t@gmail.com", LocalDate.now());
        Item item = new Item(1L, user, "Test", "Test desc", true, null);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(5L)
                .startAt(LocalDateTime.now().plusDays(1).format(formatter))
                .endAt(LocalDateTime.now().plusDays(2).format(formatter))
                .build();

        BookingDto responseDto = BookingDto.builder()
                .id(BOOKING_ID)
                .item(item)
                .booker(user)
                .startAt(requestDto.getStartAt())
                .endAt(requestDto.getEndAt())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.create(any(BookingRequestDto.class), eq(USER_ID)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(USER_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("POST /bookings — без заголовка X-Sharer-User-Id → 400")
    void createBookingWithoutUserIdHeader_shouldReturn400() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(); // минимальный объект

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────
    // PATCH /bookings/{bookingId} — одобрение/отклонение
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /bookings/{id}?approved=true — одобрение")
    void shouldApproveBooking() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(BOOKING_ID)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.reviewBooking(USER_ID, BOOKING_ID, true))
                .thenReturn(dto);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} без approved → 400")
    void reviewBookingWithoutApprovedParam_shouldReturn400() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────
    // GET /bookings/{bookingId} — получение по id
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /bookings/{id} — успешное получение")
    void shouldGetBookingById() throws Exception {
        User user = new User(1L, "Test", "t@gmail.com", LocalDate.now());
        Item item = new Item(10L, user, "Test", "Test desc", true, null);
        BookingDto dto = BookingDto.builder()
                .id(BOOKING_ID)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getById(USER_ID, BOOKING_ID)).thenReturn(dto);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.item.id").value(10))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    // ─────────────────────────────────────────────────────────────
    // GET /bookings — брони текущего пользователя
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /bookings?state=WAITING — брони пользователя")
    void shouldGetCurrentUserBookingsByState() throws Exception {
        BookingDto dto1 = BookingDto.builder().id(101L).status(BookingStatus.WAITING).build();
        BookingDto dto2 = BookingDto.builder().id(102L).status(BookingStatus.WAITING).build();

        when(bookingService.getCurrentUserBookingsByState(USER_ID, BookingState.WAITING))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/bookings")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[1].id").value(102));
    }

    @Test
    @DisplayName("GET /bookings — без state → ALL")
    void getCurrentUserBookings_defaultStateAll() throws Exception {
        when(bookingService.getCurrentUserBookingsByState(USER_ID, BookingState.ALL))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ─────────────────────────────────────────────────────────────
    // GET /bookings/owner — брони вещей владельца
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /bookings/owner?state=CURRENT")
    void shouldGetOwnerBookingsByState() throws Exception {
        BookingDto dto = BookingDto.builder().id(200L).status(BookingStatus.APPROVED).build();

        when(bookingService.getOwnerBookingsByState(USER_ID, BookingState.CURRENT))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HttpHeadersConstants.X_SHARER_USER_ID, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    // ─────────────────────────────────────────────────────────────
    // Общие негативные сценарии
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Любой эндпоинт без X-Sharer-User-Id → 400")
    void anyEndpointWithoutUserIdHeader_shouldReturn400() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());
    }
}