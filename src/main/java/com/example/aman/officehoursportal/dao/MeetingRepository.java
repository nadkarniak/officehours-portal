package com.example.aman.officehoursportal.dao;
import com.example.aman.officehoursportal.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    @Query("select a from Meeting a where a.student.id = :studentId")
    List<Meeting> findByStudentId(@Param("studentId") int studentId);

    @Query("select a from Meeting a where a.instructor.id = :instructorId")
    List<Meeting> findByInstructorId(@Param("instructorId") int instructorId);

    @Query("select a from Meeting a where a.canceler.id = :userId")
    List<Meeting> findCanceledByUser(@Param("userId") int userId);

    @Query("select a from Meeting a where  a.status='SCHEDULED' and (a.student.id = :userId or a.instructor.id = :userId)")
    List<Meeting> findScheduledByUserId(@Param("userId") int userId);

    @Query("select a from Meeting a where a.instructor.id = :instructorId and  a.start >=:dayStart and  a.start <=:dayEnd")
    List<Meeting> findByInstructorIdWithStartInPeroid(@Param("instructorId") int instructorId, @Param("dayStart") LocalDateTime startPeroid, @Param("dayEnd") LocalDateTime endPeroid);

    @Query("select a from Meeting a where a.student.id = :studentId and  a.start >=:dayStart and  a.start <=:dayEnd")
    List<Meeting> findByStudentIdWithStartInPeroid(@Param("studentId") int studentId, @Param("dayStart") LocalDateTime startPeroid, @Param("dayEnd") LocalDateTime endPeroid);

    @Query("select a from Meeting a where a.student.id = :studentId and a.canceler.id =:studentId and a.canceledAt >=:date")
    List<Meeting> findByStudentIdCanceledAfterDate(@Param("studentId") int studentId, @Param("date") LocalDateTime date);

    @Query("select a from Meeting a where a.status = 'SCHEDULED' and :now >= a.end")
    List<Meeting> findScheduledWithEndBeforeDate(@Param("now") LocalDateTime now);

    @Query("select a from Meeting a where a.status = 'SCHEDULED' and :date >= a.end and (a.student.id = :userId or a.instructor.id = :userId)")
    List<Meeting> findScheduledByUserIdWithEndBeforeDate(@Param("date") LocalDateTime date, @Param("userId") int userId);

    @Query("select a from Meeting a where a.status = 'FINISHED' and :date >= a.end")
    List<Meeting> findFinishedWithEndBeforeDate(@Param("date") LocalDateTime date);

    @Query("select a from Meeting a where a.status = 'FINISHED' and :date >= a.end and (a.student.id = :userId or a.instructor.id = :userId)")
    List<Meeting> findFinishedByUserIdWithEndBeforeDate(@Param("date") LocalDateTime date, @Param("userId") int userId);

    @Query("select a from Meeting a where a.status = 'CONFIRMED' and a.student.id = :studentId")
    List<Meeting> findConfirmedByStudentId(@Param("studentId") int studentId);

    @Query("select a from Meeting a inner join a.course w where a.status = 'SCHEDULED' and a.student.id <> :studentId and a.instructor.id= :instructorId and a.start >= :start and w.id = :courseId")
    List<Meeting> getEligibleMeetingsForExchange(@Param("start") LocalDateTime start, @Param("studentId") Integer studentId, @Param("instructorId") Integer instructorId, @Param("courseId") Integer courseId);

    @Query("select a from Meeting a where a.status = 'EXCHANGE_REQUESTED' and a.start <= :start")
    List<Meeting> findExchangeRequestedWithStartBefore(@Param("start") LocalDateTime date);

}
