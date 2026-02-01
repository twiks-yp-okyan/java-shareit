package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item mapToItem(ItemDto dto) {
        return Item.builder()
                .ownerId(dto.getOwnerId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .requestId(dto.getRequestId())
                .build();
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setOwnerId(item.getOwnerId());
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
}
