package com.example.aman.officehoursportal.dao;

import com.example.aman.officehoursportal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("select i from Report i where i.issued >= :beginingOfCurrentMonth")
    List<Report> findAllIssuedInCurrentMonth(@Param("beginingOfCurrentMonth") LocalDateTime beginingOfCurrentMonth);

    @Query("select i from Report i inner join i.meetings a where a.id in :meetingId")
    Report findByMeetingId(@Param("meetingId") int meetingId);
}
