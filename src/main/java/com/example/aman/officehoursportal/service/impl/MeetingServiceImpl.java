package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.ChatMessageRepository;
import com.example.aman.officehoursportal.dao.MeetingRepository;
import com.example.aman.officehoursportal.entity.*;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import com.example.aman.officehoursportal.exception.MeetingNotFoundException;
import com.example.aman.officehoursportal.model.DayPlan;
import com.example.aman.officehoursportal.model.TimePeroid;
import com.example.aman.officehoursportal.service.CourseService;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.NotificationService;
import com.example.aman.officehoursportal.service.UserService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final int NUMBER_OF_ALLOWED_CANCELATIONS_PER_MONTH = 1;
    private final MeetingRepository meetingRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;
    private final JwtTokenServiceImpl jwtTokenService;

    public MeetingServiceImpl(MeetingRepository meetingRepository, UserService userService, CourseService courseService, ChatMessageRepository chatMessageRepository, NotificationService notificationService, JwtTokenServiceImpl jwtTokenService) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.chatMessageRepository = chatMessageRepository;
        this.notificationService = notificationService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void updateMeeting(Meeting meeting) {
        meetingRepository.save(meeting);
    }

    @Override
    @PostAuthorize("returnObject.instructor.id == principal.id or returnObject.student.id == principal.id or hasRole('ADMIN') ")
    public Meeting getMeetingByIdWithAuthorization(int id) {
        return getMeetingById(id);
    }

    @Override
    public Meeting getMeetingById(int id) {
        return meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    @Override
    public void deleteMeetingById(int id) {
        meetingRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("#studentId == principal.id")
    public List<Meeting> getMeetingByStudentId(int studentId) {
        return meetingRepository.findByStudentId(studentId);
    }

    @Override
    @PreAuthorize("#instructorId == principal.id")
    public List<Meeting> getMeetingByInstructorId(int instructorId) {
        return meetingRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<Meeting> getMeetingsByInstructorAtDay(int instructorId, LocalDate day) {
        return meetingRepository.findByInstructorIdWithStartInPeroid(instructorId, day.atStartOfDay(), day.atStartOfDay().plusDays(1));
    }

    @Override
    public List<Meeting> getMeetingsByStudentsAtDay(int studentId, LocalDate day) {
        return meetingRepository.findByStudentIdWithStartInPeroid(studentId, day.atStartOfDay(), day.atStartOfDay().plusDays(1));
    }

    @Override
    public List<TimePeroid> getAvailableHours(int instructorId, int studentId, int courseId, LocalDate date) {
        Instructor p = userService.getInstructorById(instructorId);
        WorkingPlan workingPlan = p.getWorkingPlan();
        DayPlan selectedDay = workingPlan.getDay(date.getDayOfWeek().toString().toLowerCase());

        List<Meeting> instructorMeetings = getMeetingsByInstructorAtDay(instructorId, date);
        List<Meeting> studentMeetings = getMeetingsByStudentsAtDay(studentId, date);

        List<TimePeroid> availablePeroids = selectedDay.getTimePeroidsWithBreaksExcluded();
        availablePeroids = excludeMeetingsFromTimePeroids(availablePeroids, instructorMeetings);

        availablePeroids = excludeMeetingsFromTimePeroids(availablePeroids, studentMeetings);
        return calculateAvailableHours(availablePeroids, courseService.getCourseById(courseId));
    }

    @Override
    public void createNewMeeting(int courseId, int instructorId, int studentId, LocalDateTime start) {
        if (isAvailable(courseId, instructorId, studentId, start)) {
            Meeting meeting = new Meeting();
            meeting.setStatus(MeetingStatus.SCHEDULED);
            meeting.setStudent(userService.getStudentById(studentId));
            meeting.setInstructor(userService.getInstructorById(instructorId));
            Course course = courseService.getCourseById(courseId);
            meeting.setCourse(course);
            meeting.setStart(start);
            meeting.setEnd(start.plusMinutes(course.getDuration()));
            meetingRepository.save(meeting);
            notificationService.newNewMeetingScheduledNotification(meeting, true);
        } else {
            throw new RuntimeException();
        }

    }

    @Override
    public void addMessageToMeetingChat(int meetingId, int authorId, ChatMessage chatMessage) {
        Meeting meeting = getMeetingByIdWithAuthorization(meetingId);
        if (meeting.getInstructor().getId() == authorId || meeting.getStudent().getId() == authorId) {
            chatMessage.setAuthor(userService.getUserById(authorId));
            chatMessage.setMeeting(meeting);
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(chatMessage);
            notificationService.newChatMessageNotification(chatMessage, true);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
    }

    @Override
    public List<TimePeroid> calculateAvailableHours(List<TimePeroid> availableTimePeroids, Course course) {
        ArrayList<TimePeroid> availableHours = new ArrayList();
        for (TimePeroid peroid : availableTimePeroids) {
            TimePeroid workPeroid = new TimePeroid(peroid.getStart(), peroid.getStart().plusMinutes(course.getDuration()));
            while (workPeroid.getEnd().isBefore(peroid.getEnd()) || workPeroid.getEnd().equals(peroid.getEnd())) {
                availableHours.add(new TimePeroid(workPeroid.getStart(), workPeroid.getStart().plusMinutes(course.getDuration())));
                workPeroid.setStart(workPeroid.getStart().plusMinutes(course.getDuration()));
                workPeroid.setEnd(workPeroid.getEnd().plusMinutes(course.getDuration()));
            }
        }
        return availableHours;
    }

    @Override
    public List<TimePeroid> excludeMeetingsFromTimePeroids(List<TimePeroid> peroids, List<Meeting> meetings) {

        List<TimePeroid> toAdd = new ArrayList();
        Collections.sort(meetings);
        for (Meeting meeting : meetings) {
            for (TimePeroid peroid : peroids) {
                if ((meeting.getStart().toLocalTime().isBefore(peroid.getStart()) || meeting.getStart().toLocalTime().equals(peroid.getStart())) && meeting.getEnd().toLocalTime().isAfter(peroid.getStart()) && meeting.getEnd().toLocalTime().isBefore(peroid.getEnd())) {
                    peroid.setStart(meeting.getEnd().toLocalTime());
                }
                if (meeting.getStart().toLocalTime().isAfter(peroid.getStart()) && meeting.getStart().toLocalTime().isBefore(peroid.getEnd()) && meeting.getEnd().toLocalTime().isAfter(peroid.getEnd()) || meeting.getEnd().toLocalTime().equals(peroid.getEnd())) {
                    peroid.setEnd(meeting.getStart().toLocalTime());
                }
                if (meeting.getStart().toLocalTime().isAfter(peroid.getStart()) && meeting.getEnd().toLocalTime().isBefore(peroid.getEnd())) {
                    toAdd.add(new TimePeroid(peroid.getStart(), meeting.getStart().toLocalTime()));
                    peroid.setStart(meeting.getEnd().toLocalTime());
                }
            }
        }
        peroids.addAll(toAdd);
        Collections.sort(peroids);
        return peroids;
    }

    @Override
    public List<Meeting> getCanceledMeetingsByStudentIdForCurrentMonth(int studentId) {
        return meetingRepository.findByStudentIdCanceledAfterDate(studentId, LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
    }

    @Override
    public void updateUserMeetingsStatuses(int userId) {
        for (Meeting meeting : meetingRepository.findScheduledByUserIdWithEndBeforeDate(LocalDateTime.now(), userId)) {
            meeting.setStatus(MeetingStatus.FINISHED);
            updateMeeting(meeting);
        }

        for (Meeting meeting : meetingRepository.findFinishedByUserIdWithEndBeforeDate(LocalDateTime.now().minusDays(1), userId)) {

            meeting.setStatus(MeetingStatus.READED);
            updateMeeting(meeting);
        }
    }

    @Override
    public void updateAllMeetingsStatuses() {
        meetingRepository.findScheduledWithEndBeforeDate(LocalDateTime.now())
                .forEach(meeting -> {
                    meeting.setStatus(MeetingStatus.FINISHED);
                    updateMeeting(meeting);
                    if (LocalDateTime.now().minusDays(1).isBefore(meeting.getEnd())) {
                        notificationService.newMeetingFinishedNotification(meeting, true);
                    }
                });

        meetingRepository.findFinishedWithEndBeforeDate(LocalDateTime.now().minusDays(1))
                .forEach(meeting -> {
                    meeting.setStatus(MeetingStatus.CONFIRMED);
                    updateMeeting(meeting);
                });
    }

    @Override
    public void updateMeetingsStatusesWithExpiredExchangeRequest() {
        meetingRepository.findExchangeRequestedWithStartBefore(LocalDateTime.now().plusDays(1))
                .forEach(appointment -> {
                    appointment.setStatus(MeetingStatus.SCHEDULED);
                    updateMeeting(appointment);
                });
    }

    @Override
    public void cancelUserMeetingById(int meetingId, int userId) {
        Meeting meeting = meetingRepository.getOne(meetingId);
        if (meeting.getStudent().getId() == userId || meeting.getInstructor().getId() == userId) {
            meeting.setStatus(MeetingStatus.CANCELED);
            User canceler = userService.getUserById(userId);
            meeting.setCanceler(canceler);
            meeting.setCanceledAt(LocalDateTime.now());
            meetingRepository.save(meeting);
            if (canceler.equals(meeting.getStudent())) {
                notificationService.newMeetingCanceledByStudentNotification(meeting, true);
            } else if (canceler.equals(meeting.getInstructor())) {
                notificationService.newMeetingCanceledByInstructorNotification(meeting, true);
            }
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }


    }


    @Override
    public boolean isStudentAllowedToRejectMeeting(int userId, int meetingId) {
        User user = userService.getUserById(userId);
        Meeting meeting = getMeetingByIdWithAuthorization(meetingId);

        return meeting.getStudent().equals(user) && meeting.getStatus().equals(MeetingStatus.FINISHED) && !LocalDateTime.now().isAfter(meeting.getEnd().plusDays(1));
    }

    @Override
    public boolean requestMeetingRejection(int meetingId, int studentId) {
        if (isStudentAllowedToRejectMeeting(studentId, meetingId)) {
            Meeting meeting = getMeetingByIdWithAuthorization(meetingId);
            meeting.setStatus(MeetingStatus.REJECTION_REQUESTED);
            notificationService.newMeetingFinishedNotification(meeting, true);
            updateMeeting(meeting);
            return true;
        } else {
            return false;
        }

    }


    @Override
    public boolean requestMeetingRejection(String token) {
        if (jwtTokenService.validateToken(token)) {
            int meetingId = jwtTokenService.getMeetingIdFromToken(token);
            int studentId = jwtTokenService.getStudentIdFromToken(token);
            return requestMeetingRejection(meetingId, studentId);
        }
        return false;
    }


    @Override
    public boolean isInstructorAllowedToAcceptRejection(int instructorId, int meetingId) {
        User user = userService.getUserById(instructorId);
        Meeting meeting = getMeetingByIdWithAuthorization(meetingId);

        return meeting.getInstructor().equals(user) && meeting.getStatus().equals(MeetingStatus.REJECTION_REQUESTED);
    }

    @Override
    public boolean acceptRejection(int meetingId, int instructorId) {
        if (isInstructorAllowedToAcceptRejection(instructorId, meetingId)) {
            Meeting meeting = getMeetingByIdWithAuthorization(meetingId);
            meeting.setStatus(MeetingStatus.REJECTED);
            updateMeeting(meeting);
            notificationService.newMeetingRejectionAcceptedNotification(meeting, true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean acceptRejection(String token) {
        if (jwtTokenService.validateToken(token)) {
            int meetingId = jwtTokenService.getMeetingIdFromToken(token);
            int instructorId = jwtTokenService.getInstructorIdFromToken(token);
            return acceptRejection(meetingId, instructorId);
        }
        return false;
    }

    @Override
    public String getCancelNotAllowedReason(int userId, int meetingId) {
        User user = userService.getUserById(userId);
        Meeting meeting = getMeetingByIdWithAuthorization(meetingId);

        if (user.hasRole("ROLE_ADMIN")) {
            return "Only student or instructor can cancel meetings";
        }

        if (meeting.getInstructor().equals(user)) {
            if (!meeting.getStatus().equals(MeetingStatus.SCHEDULED)) {
                return "Only meetings with scheduled status can be cancelled.";
            } else {
                return null;
            }
        }

        if (meeting.getStudent().equals(user)) {
            if (!meeting.getStatus().equals(MeetingStatus.SCHEDULED)) {
                return "Only meetings with scheduled status can be cancelled.";
            } else if (LocalDateTime.now().plusDays(1).isAfter(meeting.getStart())) {
                return "Meetings which will be in less than 24 hours cannot be canceled.";
            } else if (!meeting.getCourse().getEditable()) {
                return "This type of meeting can be canceled only by Instructor.";
            } else if (getCanceledMeetingsByStudentIdForCurrentMonth(userId).size() >= NUMBER_OF_ALLOWED_CANCELATIONS_PER_MONTH) {
                return "You can't cancel this meeting because you exceeded maximum number of cancellations in this month.";
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public int getNumberOfCanceledMeetingsForUser(int userId) {
        return meetingRepository.findCanceledByUser(userId).size();
    }

    @Override
    public int getNumberOfScheduledMeetingsForUser(int userId) {
        return meetingRepository.findScheduledByUserId(userId).size();
    }

    @Override
    public boolean isAvailable(int courseId, int instructorId, int studentId, LocalDateTime start) {
        if (!courseService.isCourseForStudent(courseId, studentId)) {
            return false;
        }
        Course course = courseService.getCourseById(courseId);
        TimePeroid timePeroid = new TimePeroid(start.toLocalTime(), start.toLocalTime().plusMinutes(course.getDuration()));
        return getAvailableHours(instructorId, studentId, courseId, start.toLocalDate()).contains(timePeroid);
    }

    @Override
    public List<Meeting> getConfirmedMeetingByStudentId(int studentId) {
        return meetingRepository.findConfirmedByStudentId(studentId);
    }
}
