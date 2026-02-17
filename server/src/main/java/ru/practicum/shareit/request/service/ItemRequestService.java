package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    List<ItemRequestWithResponseDto> getUserRequests(Long userId);

    ItemRequestWithResponseDto getByIdWithResponse(Long requestId);

    ItemRequest getEntityById(long id);
}
