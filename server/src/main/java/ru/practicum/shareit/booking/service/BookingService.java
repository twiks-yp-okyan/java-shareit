package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingRequestDto, long userId);

    BookingDto reviewBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getCurrentUserBookingsByState(Long userId, BookingState state);

    List<BookingDto> getOwnerBookingsByState(Long userId, BookingState state);
}
