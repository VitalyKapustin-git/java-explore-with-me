package ru.practicum.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private long id;

    @NotBlank
    private String name;

    @Email
    private String email;

}
