package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.model.MeetingRegisterForm;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class AjaxController {

    private final MeetingService meetingService;
    private final NotificationService notificationService;

    public AjaxController(MeetingService meetingService, NotificationService notificationService) {
        this.meetingService = meetingService;
        this.notificationService = notificationService;
    }


    @GetMapping("/user/{userId}/meetings")
    public List<Meeting> getMeetingsForUser(@PathVariable("userId") int userId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser.hasRole("ROLE_STUDENT")) {
            return meetingService.getMeetingByStudentId(userId);
        } else if (currentUser.hasRole("ROLE_INSTRUCTOR"))
            return meetingService.getMeetingByInstructorId(userId);
        else if (currentUser.hasRole("ROLE_ADMIN"))
            return meetingService.getAllMeetings();
        else return Lists.newArrayList();
    }

    @GetMapping("/availableHours/{instructorId}/{courseId}/{date}")
    public List<MeetingRegisterForm> getAvailableHours(@PathVariable("instructorId") int instructorId, @PathVariable("courseId") int courseId, @PathVariable("date") String date, @AuthenticationPrincipal CustomUserDetails currentUser) {
        LocalDate localDate = LocalDate.parse(date);
        return meetingService.getAvailableHours(instructorId, currentUser.getId(), courseId, localDate)
                .stream()
                .map(timePeriod -> new MeetingRegisterForm(courseId, instructorId, timePeriod.getStart().atDate(localDate), timePeriod.getEnd().atDate(localDate)))
                .collect(Collectors.toList());
    }

    @GetMapping("/user/notifications")
    public int getUnreadNotificationsCount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return notificationService.getUnreadNotifications(currentUser.getId()).size();
    }

}
