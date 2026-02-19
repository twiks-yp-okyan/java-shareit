package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User user;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ItemDto> items;
}
