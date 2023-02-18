package ru.practicum.comments.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

    List<Comment> getCommentsByEvent_Id(long eventId, Pageable pageable);

    List<Comment> getCommentsByAuthor_IdAndEvent_Id(long authorId, long eventId);

    Comment getCommentById(long commentId);

    Comment getCommentByIdAndAuthor_Id(long commentId, long authorId);

    void removeCommentById(long commentId);

    @Query("select true from Comment c where c.id = ?1 and c.author.id = ?2")
    Boolean commentExists(long commentId, long authorId);

    void removeCommentByIdAndAuthor_Id(long commentId, long authorId);

    List<Comment> getCommentsByAuthor_Id(long authorId, Pageable pageable);

}
