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

    @Query("select e from Event e " +
            "where (:text is null or ((lower(e.annotation) like lower(concat('%',:text,'%'))) or (lower(e.description) like lower(concat('%',:text,'%'))))) " +
            "and e.category.id in (:categories) " +
            "and (cast(:paid as boolean) is null or e.paid = :paid) " +
            "and e.eventDate > cast(:start as timestamp) " +
            "and (cast(:end as timestamp) is null or e.eventDate < cast(:end as timestamp)) " +
            "and e.state = 'PUBLISHED' " +
            "and (cast(:onlyAvailable as boolean) = false or e.participantLimit = 0 " +
            "or e.participantLimit > (select count(r) from Request r where r.event.id = e.id and r.status = 'CONFIRMED'))")
    Page<Event> search(@Param("text") String text,
                       @Param("categories") List<Long> categories,
                       @Param("paid") Boolean paid,
                       @Param("start") LocalDateTime rangeStart,
                       @Param("end") LocalDateTime rangeEnd,
                       @Param("onlyAvailable") Boolean onlyAvailable,
                       Pageable pageable);

}