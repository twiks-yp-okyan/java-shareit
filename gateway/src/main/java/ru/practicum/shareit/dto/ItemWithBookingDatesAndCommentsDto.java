package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithBookingDatesAndCommentsDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("lastBooking")
    private LocalDateTime lastBookingDate;
    @JsonProperty("nextBooking")
    private LocalDateTime nextBookingDate;
    private List<CommentDto> comments;
    private Long requestId;
}
