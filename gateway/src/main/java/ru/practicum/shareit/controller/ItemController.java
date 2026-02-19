package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemUpdateDto;
import ru.practicum.shareit.utils.HttpHeadersConstants;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto newItem
    ) {
        return itemClient.create(userId, newItem);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserItems(@RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable long id,
            @Valid @RequestBody ItemUpdateDto updatedItem
    ) {
        return itemClient.update(userId, id, updatedItem);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable long id
    ) {
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsBySearchText(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "text") String searchText
    ) {
        return itemClient.getItemsBySearchText(userId, searchText);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentRequest
    ) {
        return itemClient.addNewComment(userId, itemId, commentRequest);
    }
}
