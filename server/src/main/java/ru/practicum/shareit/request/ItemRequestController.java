package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.HttpHeadersConstants;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "from") int from,
            @RequestParam(value = "size") int size
    ) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestWithResponseDto> getUserRequests(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponseDto getById(
            @RequestHeader(HttpHeadersConstants.X_SHARER_USER_ID) Long userId,
            @PathVariable(value = "requestId") Long requestId
    ) {
        return itemRequestService.getByIdWithResponse(userId, requestId);
    }
}
