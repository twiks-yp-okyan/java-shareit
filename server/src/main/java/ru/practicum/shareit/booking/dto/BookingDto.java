package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.validator.annotation.DateInFuture;

@Data
@AllArgsConstructor
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty("start")
    @NotNull
    @DateInFuture
    private String startAt;
    @JsonProperty("end")
    @NotNull
    @DateInFuture
    private String endAt;
    @NotNull
    private Item item;
    private User booker;
    private BookingStatus status;
}
