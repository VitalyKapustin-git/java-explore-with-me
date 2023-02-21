package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CommentNewDto {

    @NotBlank
    private String comment;

}
