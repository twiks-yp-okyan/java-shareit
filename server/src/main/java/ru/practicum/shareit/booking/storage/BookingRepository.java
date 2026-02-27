package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long userId);

    List<Booking> findByItemOwnerId(Long userId);

    @Query("""
            select b
            from Booking b
            where b.item.id in :itemIds
            and b.endAt < current_timestamp
            and b.status = :status
            and b.startAt = (select max(b2.startAt)
                            from Booking b2
                            where b2.item.id = b.item.id
                            and b.endAt < current_timestamp
                            and b2.status = :status)
            """)
    List<Booking> findLastItemsBooking(@Param("itemIds") List<Long> itemIds, @Param("status") BookingStatus status);

    @Query("""
            select b
            from Booking b
            where b.item.id in :itemIds
            and b.startAt > current_timestamp
            and b.status = :status
            and b.startAt = (select min(b2.startAt)
                            from Booking b2
                            where b2.item.id = b.item.id
                            and b2.startAt > current_timestamp
                            and b2.status = :status)
            """)
    List<Booking> findNextItemsBooking(@Param("itemIds") List<Long> itemIds, @Param("status") BookingStatus status);
}
