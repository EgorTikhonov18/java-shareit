package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;

public class ItemRequestValidation {

    public boolean itemRequestValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        return descriptionValidation(requestBodyRequestDto);
    }

    private boolean descriptionValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        if (requestBodyRequestDto.getDescription() == null) {
            return false;
        } else {
            return !requestBodyRequestDto.getDescription().equals("") && !requestBodyRequestDto.getDescription().equals(" ");
        }
    }
}
