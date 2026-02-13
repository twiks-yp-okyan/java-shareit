package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BookingConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public List<ItemWithBookingDatesAndCommentsDto> getUserItems(Long userId) {
        User user = userService.getEntityById(userId);
        List<Item> userItems = repository.findByOwnerId(user.getId());
        List<Long> userItemsIds = userItems.stream().map(Item::getId).toList();
        Map<Long, Booking> lastBookings = bookingRepository.findLastItemsBooking(userItemsIds, BookingStatus.APPROVED).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, Booking> nextBookings = bookingRepository.findNextItemsBooking(userItemsIds, BookingStatus.APPROVED).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        return userItems.stream()
                .map(item -> ItemMapper.mapToDtoWithDates(
                        item, lastBookings.get(item.getId()), nextBookings.get(item.getId())
                ))
                .toList();
    }

    @Transactional
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
    public ItemWithBookingDatesAndCommentsDto getItemById(Long itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapToDto)
                .toList();
        ItemWithBookingDatesAndCommentsDto response = ItemMapper.mapToDtoWithDates(item, null, null);
        response.setComments(comments);
        return response;
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

    @Transactional
    @Override
    public CommentDto addNewComment(Long userId, Long itemId, CommentDto commentRequest) {
        final User user = userService.getEntityById(userId);
        Item item = getEntityById(itemId);
        bookingRepository.findByBookerId(user.getId()).stream()
            .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED)
                    && booking.getEndAt().isBefore(LocalDateTime.now())
                    && booking.getBooker().getId().equals(user.getId()))
            .findAny()
            .orElseThrow(() -> new BookingConflictException(
                    String.format("У пользователя с id = %d нет завершенных аренд вещи с id = %d", user.getId(), item.getId())
            ));

        Comment comment = CommentMapper.mapToComment(commentRequest);
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);
        return CommentMapper.mapToDto(comment);
    }
}
