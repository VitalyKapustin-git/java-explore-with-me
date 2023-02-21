package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentShortDto {

    private long id;

    private long author;

    private long event;

    private String comment;

}
