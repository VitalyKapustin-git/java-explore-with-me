package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Event getEventById(long eventId);

    List<Event> getEventsByInitiatorId(long userId, Pageable pageable);

    Event getEventByIdAndInitiator_Id(long eventId, long userId);

    List<Event> getEventsByIdIn(List<Long> ids);

}
