package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stats;
import ru.practicum.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.StatsViewDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :startTime AND :endTime " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsViewDto> findAllByTimeAndListOfUris(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime,
                                                  @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.StatsViewDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :startTime AND :endTime " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsViewDto> findAllByTimeAndListOfUrisAndUniqueIp(@Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime,
                                                             @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.StatsViewDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :startTime AND :endTime " +
            "GROUP BY s.app, s.uri")
    List<StatsViewDto> findAllByTime(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Query("SELECT new ru.practicum.StatsViewDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :startTime AND :endTime " +
            "GROUP BY s.app, s.uri")
    List<StatsViewDto> findAllByTimeAndUniqueIp(@Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

}