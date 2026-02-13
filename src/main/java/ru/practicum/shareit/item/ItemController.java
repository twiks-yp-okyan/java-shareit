package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto newItem
    ) {
        return itemService.create(userId, newItem);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemWithBookingDatesAndCommentsDto> getUserItems(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId) {
        return itemService.getUserItems(userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable long id,
            @RequestBody ItemUpdateDto updatedItem
    ) {
        return itemService.update(userId, id, updatedItem);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemWithBookingDatesAndCommentsDto getItemById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable long id
    ) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsBySearchText(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "text") String searchText
    ) {
        return itemService.getItemsBySearchText(searchText);
    }

    @GetMapping("/all")
    public Page<ItemDto> getAllItems(Pageable pageable) {
        return itemService.getAllItems(pageable);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentRequest
    ) {
        return itemService.addNewComment(userId, itemId, commentRequest);
    }
}
