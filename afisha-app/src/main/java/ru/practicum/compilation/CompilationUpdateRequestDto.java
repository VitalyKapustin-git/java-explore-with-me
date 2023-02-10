package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateRequestDto {

    List<Long> events;

    Boolean pinned;

    String title;

}
