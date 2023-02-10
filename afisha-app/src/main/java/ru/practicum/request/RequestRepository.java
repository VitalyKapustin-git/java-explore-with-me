package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request getRequestById(long requestId);

    @Query("select r from Request r where r.requester.id = ?1 and r.event.initiator.id <> ?1")
    List<Request> getOwnRequestsForNotOwnEvents(long userId);

    @Query("update Request r set r.status = ?3 where r.requester.id = ?1 and r.id = ?2")
    @Modifying
    void revokeOwnRequest(long userId, long requestId, String status);

    List<Request> getRequestsByEvent_IdAndEvent_Initiator_Id(long eventId, long userId);

    @Query("update Request r set r.status = ?2 where r.id in (?1)")
    @Modifying
    void updateRequestStatus(List<Long> requestId, String status);

    @Query("select true from Request r where r.requester.id = ?1 and r.event.id = ?2")
    Boolean checkIfUserRequestAlreadyExists(long userId, long eventId);

    @Query("select r.requester.id from Request r where r.id = ?1")
    long getRequesterId(long requestId);

}
