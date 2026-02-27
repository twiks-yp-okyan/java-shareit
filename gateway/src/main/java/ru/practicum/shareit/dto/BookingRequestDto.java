package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @JsonProperty("start")
    @NotNull
    @FutureOrPresent
    private LocalDateTime startAt;
    @JsonProperty("end")
    @NotNull
    @Future
    private LocalDateTime endAt;
}
