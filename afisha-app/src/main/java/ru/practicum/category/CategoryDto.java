package ru.practicum.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private long id;

    @NotBlank
    private String name;

}
