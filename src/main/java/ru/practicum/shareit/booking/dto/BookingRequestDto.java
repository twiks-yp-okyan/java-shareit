package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.validator.annotation.DateInFuture;

@Data
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @JsonProperty("start")
    @NotNull
    @DateInFuture
    private String startAt;
    @JsonProperty("end")
    @NotNull
    @DateInFuture
    private String endAt;
}
