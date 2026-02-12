package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto newItem);

    List<ItemDto> getUserItems(Long userId);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto updatedItem);

    ItemDto getItemById(Long itemId);

    Item getEntityById(Long id);

    List<ItemDto> getItemsBySearchText(String searchText);

    Page<ItemDto> getAllItems(Pageable pageable);
}
