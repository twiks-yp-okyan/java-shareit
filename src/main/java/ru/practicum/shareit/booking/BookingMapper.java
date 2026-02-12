package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Booking mapToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStartAt(LocalDateTime.parse(dto.getStartAt(), formatter));
        booking.setEndAt(LocalDateTime.parse(dto.getEndAt(), formatter));
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public static Booking mapToNewBooking(BookingRequestDto dto) {
        Booking booking = new Booking();
        booking.setStartAt(LocalDateTime.parse(dto.getStartAt(), formatter));
        booking.setEndAt(LocalDateTime.parse(dto.getEndAt(), formatter));
        return booking;
    }

    public static BookingDto mapToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartAt().format(formatter),
                booking.getEndAt().format(formatter),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
}
