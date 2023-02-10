package ru.practicum.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    Compilation getCompilationById(long compId);

    List<Compilation> getCompilationsByPinnedIsTrue(Pageable pageable);

    List<Compilation> getCompilationsByPinnedIsFalse(Pageable pageable);

    void removeCompilationById(long id);

}
