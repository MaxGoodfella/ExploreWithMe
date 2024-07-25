package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.enums.EventStatus;
import ru.practicum.event.model.models.Event;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(List<Long> users,
                                                                                   List<EventStatus> states,
                                                                                   List<Long> categories,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsBefore(List<Long> users,
                                                                                    List<EventStatus> states,
                                                                                    List<Long> categories,
                                                                                    LocalDateTime end,
                                                                                    Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfter(List<Long> users,
                                                                                   List<EventStatus> states,
                                                                                   List<Long> categories,
                                                                                   LocalDateTime start,
                                                                                   Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdIn(List<Long> users,
                                                                List<EventStatus> states,
                                                                List<Long> categories,
                                                                Pageable pageable);



    @Query("SELECT COUNT(r.id) FROM Request r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    Integer countConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:text IS NULL OR ((lower(e.annotation) LIKE lower(concat('%',:text,'%'))) OR (lower(e.description) LIKE lower(concat('%',:text,'%'))))) " +
            "AND e.category.id IN (:categories) " +
            "AND (cast(:paid AS boolean) IS NULL OR e.paid = :paid) " +
            "AND e.eventDate > cast(:start AS TIMESTAMP) " +
            "AND (cast(:end AS TIMESTAMP) IS NULL OR e.eventDate < cast(:end AS TIMESTAMP)) " +
            "AND e.state = 'PUBLISHED' " +
            "AND (cast(:onlyAvailable AS boolean) = FALSE OR e.participantLimit = 0 " +
            "OR e.participantLimit > (SELECT COUNT(r) FROM Request r WHERE r.event.id = e.id AND r.status = 'CONFIRMED'))")
    Page<Event> search(@Param("text") String text,
                       @Param("categories") List<Long> categories,
                       @Param("paid") Boolean paid,
                       @Param("start") LocalDateTime rangeStart,
                       @Param("end") LocalDateTime rangeEnd,
                       @Param("onlyAvailable") Boolean onlyAvailable,
                       Pageable pageable);

}