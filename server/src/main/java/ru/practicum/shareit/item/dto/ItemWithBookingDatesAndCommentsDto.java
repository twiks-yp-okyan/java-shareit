package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
