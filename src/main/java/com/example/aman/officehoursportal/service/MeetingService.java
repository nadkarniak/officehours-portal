package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.ChatMessage;
import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.model.TimePeroid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    void createNewMeeting(int courseId, int instructorId, int studentId, LocalDateTime start);

    void updateMeeting(Meeting meeting);

    void updateUserMeetingsStatuses(int userId);

    void updateAllMeetingsStatuses();

    void updateMeetingsStatusesWithExpiredExchangeRequest();

    void deleteMeetingById(int meetingId);

    Meeting getMeetingByIdWithAuthorization(int id);

    Meeting getMeetingById(int id);

    List<Meeting> getAllMeetings();

    List<Meeting> getMeetingByStudentId(int studentId);

    List<Meeting> getMeetingByInstructorId(int instructorId);

    List<Meeting> getMeetingsByInstructorAtDay(int instructorId, LocalDate day);

    List<Meeting> getMeetingsByStudentsAtDay(int instructorId, LocalDate day);

    List<Meeting> getConfirmedMeetingByStudentId(int studentId);

    List<Meeting> getCanceledMeetingsByStudentIdForCurrentMonth(int userId);

    List<TimePeroid> getAvailableHours(int instructorId, int studentId, int courseId, LocalDate date);

    List<TimePeroid> calculateAvailableHours(List<TimePeroid> availableTimePeroids, Course course);

    List<TimePeroid> excludeMeetingsFromTimePeroids(List<TimePeroid> peroids, List<Meeting> meetings);

    String getCancelNotAllowedReason(int userId, int meetingId);

    void cancelUserMeetingById(int meetingId, int userId);

    boolean isStudentAllowedToRejectMeeting(int studentId, int meetingId);

    boolean requestMeetingRejection(int meetingId, int studentId);

    boolean requestMeetingRejection(String token);

    boolean isInstructorAllowedToAcceptRejection(int instructorId, int meetingId);

    boolean acceptRejection(int meetingId, int instructorId);

    boolean acceptRejection(String token);

    void addMessageToMeetingChat(int meetingId, int authorId, ChatMessage chatMessage);

    int getNumberOfCanceledMeetingsForUser(int userId);

    int getNumberOfScheduledMeetingsForUser(int userId);


    boolean isAvailable(int workId, int instructorId, int studentId, LocalDateTime start);
}
