package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto newItem) {
        log.debug("Trying to save new Item by user {}: {}", userId, newItem.toString());
        User user = userService.getEntityById(userId);
        Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(user);
        item = repository.save(item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return repository.findByOwnerId(userId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto updatedItem) {
        log.debug("Trying to update Item by user {}: {}", userId, updatedItem.toString());
        Item currentItem = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        if (!currentItem.getOwner().getId().equals(userId)) {
            throw new UserNotOwnerException(
                    String.format("Пользователь с id = %d не является владельцем ващи с id = %d", userId, itemId)
            );
        }
        ItemMapper.updateItemData(currentItem, updatedItem);
        currentItem = repository.save(currentItem);
        return ItemMapper.mapToDto(currentItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return repository.findById(itemId)
                .map(ItemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
    }

    @Override
    public Item getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", id)));
    }

    @Override
    public List<ItemDto> getItemsBySearchText(String searchText) {
        List<Item> foundItems = searchText.isBlank() ? new ArrayList<>() : repository.findByNameOrDescription(searchText.toLowerCase());
        return foundItems.stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public Page<ItemDto> getAllItems(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ItemMapper::mapToDto);
    }
}
