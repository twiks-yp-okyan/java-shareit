package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long itemId;
    private String authorName;
    @NotBlank
    private String text;
    @JsonProperty(value = "created", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
