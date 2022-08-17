package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
