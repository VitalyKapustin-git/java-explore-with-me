package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Event getEventById(long eventId);

    @Query("select e from Event e where e.initiator.id = ?1")
    List<Event> getEventsByInitiatorId(long userId, Pageable pageable);

    Event getEventByIdAndInitiator_Id(long eventId, long userId);

    List<Event> getEventsByIdIn(List<Long> ids);


}
