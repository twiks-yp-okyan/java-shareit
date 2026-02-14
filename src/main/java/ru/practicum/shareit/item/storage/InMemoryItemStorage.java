package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.IdGenerator;

import java.util.*;

@Repository("inMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        Long id = IdGenerator.getNextId(items);
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> userItems = new ArrayList<>();
//        for (Item item : items.values()) {
//            if (item.getOwnerId().equals(userId)) {
//                userItems.add(item);
//            }
//        }
        return userItems;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        Item item = items.getOrDefault(itemId, null);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    @Override
    public List<Item> getItemsBySearchText(String searchText) {
        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(searchText) || item.getDescription().toLowerCase().contains(searchText))
                    && item.getAvailable().equals(true)) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

    @Override
    public List<Item> getAllItems() {
        return items.values().stream().toList();
    }
}
