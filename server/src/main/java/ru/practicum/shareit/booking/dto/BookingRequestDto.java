package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.validator.annotation.DateInFuture;

@Data
@AllArgsConstructor
public class BookingRequestDto {
    private Long itemId;
    @JsonProperty("start")
    private String startAt;
    @JsonProperty("end")
    private String endAt;
}
