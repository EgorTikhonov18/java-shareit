package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;

@Slf4j
@Component
public class ItemRequestValidation {

    public void itemRequestValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        descriptionValidation(requestBodyRequestDto);
    }

    private void descriptionValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        if (requestBodyRequestDto.getDescription() == null || requestBodyRequestDto.getDescription().equals("") ||
                requestBodyRequestDto.getDescription().equals(" ")) {
            String message = "Описание должно быть указано";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}