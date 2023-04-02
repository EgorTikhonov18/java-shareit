package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotEmpty
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
