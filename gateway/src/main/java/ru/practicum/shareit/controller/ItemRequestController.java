package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HttpHeadersConstants;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable(value = "requestId") Long requestId
    ) {
        return itemRequestClient.getById(userId, requestId);
    }
}
