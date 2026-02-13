package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item mapToItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setOwnerId(item.getOwner().getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    public static void updateItemData(Item currentItem, ItemUpdateDto updatedItem) {
        if (updatedItem.hasName()) {
            currentItem.setName(updatedItem.getName());
        }
        if (updatedItem.hasDescription()) {
            currentItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.hasAvailable()) {
            currentItem.setAvailable(updatedItem.getAvailable());
        }
    }

    public static ItemWithBookingDatesAndCommentsDto mapToDtoWithDates(Item item, Booking lastBooking, Booking nextBooking) {
        ItemWithBookingDatesAndCommentsDto dto = new ItemWithBookingDatesAndCommentsDto();
        dto.setId(item.getId());
        dto.setOwnerId(item.getOwner().getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        LocalDateTime lastBookingStartAt = lastBooking == null ? null : lastBooking.getStartAt();
        dto.setLastBookingDate(lastBookingStartAt);
        LocalDateTime nextBookingStartAt = nextBooking == null ? null : nextBooking.getStartAt();
        dto.setNextBookingDate(nextBookingStartAt);
        dto.setRequestId(item.getRequestId());
        return dto;
    }
}
