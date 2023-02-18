package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateDto {

    @NotNull(message = "You must specify id of comment which you want to update")
    private Long id;

    @NotBlank
    private String comment;

}
