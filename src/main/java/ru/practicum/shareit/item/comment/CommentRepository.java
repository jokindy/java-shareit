package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByItemId(int id);
}
