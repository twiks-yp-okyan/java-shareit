package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto reviewBooking(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam(value = "approved") Boolean isApproved
    ) {
        return bookingService.reviewBooking(userId, bookingId, isApproved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getCurrentUserBookingsByState(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getCurrentUserBookingsByState(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getOwnerBookingsByState(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getOwnerBookingsByState(userId, state);
    }
}