package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Collection<Item> getUserItems(Long userId);

    Item update(Item item);

    Optional<Item> getItemById(Long itemId);

    Collection<Item> getItemsBySearchText(String searchText);

    Collection<Item> getAllItems();
}
