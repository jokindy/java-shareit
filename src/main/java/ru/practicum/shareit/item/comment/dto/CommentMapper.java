package ru.practicum.shareit.item.comment.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<CommentDto> toDtoList(Collection<Comment> comments) {
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    public Comment toDomain(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

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
    }
}
