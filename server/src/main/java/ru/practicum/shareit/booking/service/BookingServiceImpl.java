package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BookingConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public BookingDto create(BookingRequestDto dto, long userId) {
        Booking booking = BookingMapper.mapToNewBooking(dto);
        if (booking.getStartAt().equals(booking.getEndAt())) {
            throw new BookingConflictException("У бронирования совпадают даты начала и завершения");
        }
        User booker = userService.getEntityById(userId);
        Item item = itemService.getEntityById(dto.getItemId());
        if (!item.getAvailable()) {
            throw new BookingConflictException(String.format("Товар с id = %d не доступен для бронирования", item.getId()));
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking = repository.save(booking);
        return BookingMapper.mapToDto(booking);
    }

    @Transactional
    @Override
    public BookingDto reviewBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = getEntityById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotOwnerException(
                    String.format("Пользователь с id = %d не является владельцем ващи с id = %d", userId, booking.getItem().getId())
            );
        }
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        booking = repository.save(booking);
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = getEntityById(bookingId);
        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new BookingConflictException(
                    String.format("Пользователь с id = %d не является ни арендатором, ни арендодателем для вещи с id = %d", userId, booking.getItem().getId())
            );
        }
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getCurrentUserBookingsByState(Long userId, BookingState state) {
        User currentUser = userService.getEntityById(userId);
        List<Booking> userBookings = repository.findByBookerId(currentUser.getId());
        Stream<Booking> userBookingsStream = filterBookingsByStateAndSort(userBookings, state);
        return userBookingsStream.map(BookingMapper::mapToDto).toList();
    }

    @Override
    public List<BookingDto> getOwnerBookingsByState(Long userId, BookingState state) {
        User owner = userService.getEntityById(userId);
        List<Booking> userBookings = repository.findByItemOwnerId(owner.getId());
        Stream<Booking> userBookingsStream = filterBookingsByStateAndSort(userBookings, state);
        return userBookingsStream.map(BookingMapper::mapToDto).toList();
    }

    private Booking getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id = %d не найдено", id)));
    }

    private Stream<Booking> filterBookingsByStateAndSort(List<Booking> bookings, BookingState state) {
        final LocalDateTime currentTime = LocalDateTime.now();
        Stream<Booking> bookingsStream = switch (state) {
            case CURRENT -> bookings.stream()
                    .filter(booking -> booking.getStartAt().isBefore(currentTime)
                            && booking.getEndAt().isAfter(currentTime));
            case PAST -> bookings.stream()
                    .filter(booking -> booking.getEndAt().isBefore(currentTime));
            case FUTURE -> bookings.stream()
                    .filter(booking -> booking.getStartAt().isAfter(currentTime));
            case WAITING -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING));
            case REJECTED -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED));
            default -> bookings.stream();
        };
        return bookingsStream.sorted(Comparator.comparing(Booking::getStartAt).reversed());
    }
}
