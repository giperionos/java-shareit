package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String serverStatusCode;
    private String description;
}
