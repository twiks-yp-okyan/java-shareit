package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.getEntityById(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setUser(user);
        itemRequest = repository.save(itemRequest);
        return ItemRequestMapper.mapToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return repository.findAll(page)
                .stream().sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(ItemRequestMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemRequestWithResponseDto> getUserRequests(Long userId) {
        List<ItemRequest> userRequests = repository.findByUserId(userId);
        List<Long> userRequestsIds = userRequests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<ItemDto>> requestsResponses = itemService.getItemsByRequestIds(userRequestsIds).stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return userRequests.stream()
                .map(userRequest -> ItemRequestMapper.mapToResponseDto(userRequest, requestsResponses.get(userRequest.getId())))
                .toList();
    }

    @Override
    public ItemRequestWithResponseDto getById(Long requestId) {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с id = %d не существует", requestId)));
        List<ItemDto> requestResponses = itemService.getItemsByRequestIds(List.of(requestId));
        return ItemRequestMapper.mapToResponseDto(request, requestResponses);
    }
}
