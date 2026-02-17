package ru.practicum.shareit.dto;

import lombok.Data;

@Data
public class ItemUpdateDto {
    private final String name;
    private final String description;
    private final Boolean available;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
