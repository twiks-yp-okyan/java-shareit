package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Qualifier("inMemoryItemStorage")
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto newItem) {
        UserDto user = userService.getById(userId);
        newItem.setOwnerId(user.getId());
        Item item = ItemMapper.mapToItem(newItem);
        return ItemMapper.mapToDto(itemStorage.create(item));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.getUserItems(userId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto updatedItem) {
        Item currentItem = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        if (!currentItem.getOwnerId().equals(userId)) {
            throw new UserNotOwnerException(
                    String.format("Пользователь с id = %d не является владельцем ващи с id = %d", userId, itemId)
            );
        }
        ItemMapper.updateItemData(currentItem, updatedItem);
        currentItem = itemStorage.update(currentItem);
        return ItemMapper.mapToDto(currentItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId)
                .map(ItemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
    }
}
