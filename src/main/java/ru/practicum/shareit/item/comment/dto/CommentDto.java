package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private int id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
