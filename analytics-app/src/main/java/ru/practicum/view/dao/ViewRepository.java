package ru.practicum.view.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.view.dto.StatsDto;
import ru.practicum.view.model.View;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewRepository extends JpaRepository<View, Long> {

    @Query("select new ru.practicum.view.dto.StatsDto(v.app, v.uri, count(v.ip)) from View v " +
            "where v.date between ?1 and ?2 group by v.app, v.uri")
    List<StatsDto> getAllViewsNonUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.view.dto.StatsDto(v.app, v.uri, count(distinct v.ip)) from View v " +
            "where v.date between ?1 and ?2 group by v.app, v.uri")
    List<StatsDto> getAllViewsUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.view.dto.StatsDto(v.app, v.uri, count(distinct v.ip)) from View v " +
            "where (v.date between ?1 and ?2) " +
            "and v.uri in ?3 " +
            "group by v.app, v.uri " +
            "order by count(distinct v.ip) DESC")
    List<StatsDto> getViewsUniqueByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.view.dto.StatsDto(v.app, v.uri, count(v.ip)) from View v " +
            "where (v.date between ?1 and ?2) " +
            "and v.uri in ?3 " +
            "group by v.app, v.uri " +
            "order by count(v.ip) DESC")
    List<StatsDto> getViewsNonUniqueByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}
