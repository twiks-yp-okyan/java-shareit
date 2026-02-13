package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto newItem);

    List<ItemWithBookingDatesAndCommentsDto> getUserItems(Long userId);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto updatedItem);

    ItemWithBookingDatesAndCommentsDto getItemById(Long itemId);

    Item getEntityById(Long id);

    List<ItemDto> getItemsBySearchText(String searchText);

    Page<ItemDto> getAllItems(Pageable pageable);

    CommentDto addNewComment(Long userId, Long itemId, CommentDto commentRequest);
}
