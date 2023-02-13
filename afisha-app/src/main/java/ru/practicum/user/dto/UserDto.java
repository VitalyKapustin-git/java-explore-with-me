package ru.practicum.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(message = "Name shouldn't be blank")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

}
