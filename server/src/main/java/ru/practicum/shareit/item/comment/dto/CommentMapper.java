package ru.practicum.shareit.item.comment.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public CommentMapper(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        setUp();
    }


    public CommentDto toDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public Comment toDomain(CommentDto commentDto, int itemId, int authorId) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        return comment;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void setUp() {
        modelMapper.createTypeMap(Comment.class, CommentDto.class).setPostConverter(
                ctx -> {
                    int userId = ctx.getSource().getAuthorId();
                    String name = userRepository.findById(userId).get().getName();
                    CommentDto dto = ctx.getDestination();
                    dto.setAuthorName(name);
                    return dto;
                }
        );
        modelMapper.createTypeMap(CommentDto.class, Comment.class).setPostConverter(
                ctx -> {
                    Comment comment = ctx.getDestination();
                    comment.setCreated(LocalDateTime.now());
                    return comment;
                }
        );
    }
}
