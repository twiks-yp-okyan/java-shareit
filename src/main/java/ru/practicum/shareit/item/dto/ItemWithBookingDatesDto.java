package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemWithBookingDatesDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBookingDate;
    private LocalDateTime nextBookingDate;
    private Long requestId;
}
