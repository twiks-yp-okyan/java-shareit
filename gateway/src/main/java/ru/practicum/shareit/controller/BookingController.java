package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingRequestDto;
import ru.practicum.shareit.dto.BookingState;
import ru.practicum.shareit.utils.HttpHeadersConstants;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return bookingClient.create(userId, bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> reviewBooking(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam(value = "approved") Boolean isApproved
    ) {
        return bookingClient.reviewBooking(userId, bookingId, isApproved);
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getCurrentUserBookingsByState(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingClient.getCurrentUserBookingsByState(userId, state);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getOwnerBookingsByState(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state
    ) {
        return bookingClient.getOwnerBookingsByState(userId, state);
    }
}