package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Meeting;

import java.time.LocalDateTime;
import java.util.Date;

public interface JwtTokenService {
    String generateMeetingRejectionToken(Meeting meeting);

    String generateAcceptRejectionToken(Meeting meeting);

    boolean validateToken(String token);

    int getMeetingIdFromToken(String token);

    int getStudentIdFromToken(String token);

    int getInstructorIdFromToken(String token);

    ////
    Date convertLocalDateTimeToDate(LocalDateTime localDateTime);
}
