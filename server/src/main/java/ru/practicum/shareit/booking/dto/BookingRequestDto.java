package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingRequestDto {
    private Long itemId;
    @JsonProperty("start")
    private String startAt;
    @JsonProperty("end")
    private String endAt;
}
