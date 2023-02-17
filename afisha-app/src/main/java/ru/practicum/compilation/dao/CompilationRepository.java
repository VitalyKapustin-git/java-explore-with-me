package ru.practicum.compilation.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    Compilation getCompilationById(long compId);

    List<Compilation> getCompilationsByPinnedIs(Boolean isPinned, Pageable pageable);

    void removeCompilationById(long id);

}
