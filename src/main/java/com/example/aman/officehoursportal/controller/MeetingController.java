package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.ChatMessage;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.ExchangeService;
import com.example.aman.officehoursportal.service.UserService;
import com.example.aman.officehoursportal.service.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/meetings")
public class MeetingController {

    private static final String REJECTION_CONFIRMATION_VIEW = "meetings/rejectionConfirmation";

    private final CourseService courseService;
    private final UserService userService;
    private final MeetingService meetingService;
    private final ExchangeService exchangeService;

    public MeetingController(CourseService courseService, UserService userService, MeetingService meetingService, ExchangeService exchangeService) {
        this.courseService = courseService;
        this.userService = userService;
        this.meetingService = meetingService;
        this.exchangeService = exchangeService;
    }

    @GetMapping("/all")
    public String showAllMeetings(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        String meetingsModelName = "meetings";

        if (currentUser.hasRole("ROLE_STUDENT")) {
            model.addAttribute(meetingsModelName, meetingService.getMeetingByStudentId(currentUser.getId()));
        } else if (currentUser.hasRole("ROLE_INSTRUCTOR")) {
            model.addAttribute(meetingsModelName, meetingService.getMeetingByInstructorId(currentUser.getId()));
        } else if (currentUser.hasRole("ROLE_ADMIN")) {
            model.addAttribute(meetingsModelName, meetingService.getAllMeetings());
        }
        return "meetings/listMeetings";
    }

    @GetMapping("/{id}")
    public String showMeetingDetail(@PathVariable("id") int meetingId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Meeting meeting = meetingService.getMeetingByIdWithAuthorization(meetingId);
        model.addAttribute("meeting", meeting);
        model.addAttribute("chatMessage", new ChatMessage());
        boolean allowedToRequestRejection = meetingService.isStudentAllowedToRejectMeeting(currentUser.getId(), meetingId);
        boolean allowedToAcceptRejection = meetingService.isInstructorAllowedToAcceptRejection(currentUser.getId(), meetingId);
        boolean allowedToExchange = exchangeService.checkIfEligibleForExchange(currentUser.getId(), meetingId);
        model.addAttribute("allowedToRequestRejection", allowedToRequestRejection);
        model.addAttribute("allowedToAcceptRejection", allowedToAcceptRejection);
        model.addAttribute("allowedToExchange", allowedToExchange);
        if (allowedToRequestRejection) {
            model.addAttribute("remainingTime", formatDuration(Duration.between(LocalDateTime.now(), meeting.getEnd().plusDays(1))));
        }
        String cancelNotAllowedReason = meetingService.getCancelNotAllowedReason(currentUser.getId(), meetingId);
        model.addAttribute("allowedToCancel", cancelNotAllowedReason == null);
        model.addAttribute("cancelNotAllowedReason", cancelNotAllowedReason);
        return "meetings/meetingDetail";
    }


    @PostMapping("/reject")
    public String processMeetingRejectionRequest(@RequestParam("meetingId") int meetingId, @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        boolean result = meetingService.requestMeetingRejection(meetingId, currentUser.getId());
        model.addAttribute(result);
        model.addAttribute("type", "request");
        return REJECTION_CONFIRMATION_VIEW;
    }

    @GetMapping("/reject")
    public String processMeetingRejectionRequest(@RequestParam("token") String token, Model model) {
        boolean result = meetingService.requestMeetingRejection(token);
        model.addAttribute(result);
        model.addAttribute("type", "request");
        return REJECTION_CONFIRMATION_VIEW;
    }

    @PostMapping("/acceptRejection")
    public String acceptMeetingRejectionRequest(@RequestParam("meetingId") int meetingId, @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        boolean result = meetingService.acceptRejection(meetingId, currentUser.getId());
        model.addAttribute(result);
        model.addAttribute("type", "accept");
        return REJECTION_CONFIRMATION_VIEW;
    }

    @GetMapping("/acceptRejection")
    public String acceptMeetingRejectionRequest(@RequestParam("token") String token, Model model) {
        boolean result = meetingService.acceptRejection(token);
        model.addAttribute(result);
        model.addAttribute("type", "accept");
        return REJECTION_CONFIRMATION_VIEW;
    }

    @PostMapping("/messages/new")
    public String addNewChatMessage(@ModelAttribute("chatMessage") ChatMessage chatMessage, @RequestParam("meetingId") int meetingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        int authorId = currentUser.getId();
        meetingService.addMessageToMeetingChat(meetingId, authorId, chatMessage);
        return "redirect:/meetings/" + meetingId;
    }

    @GetMapping("/new")
    public String selectInstructor(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser.hasRole("ROLE_STUDENT_UNDERGRAD")) {
            model.addAttribute("instructors", userService.getInstructorsWithUndergradCourses());
        } else if (currentUser.hasRole("ROLE_STUDENT_GRAD")) {
            model.addAttribute("instructors", userService.getInstructorsWithGradCourses());
        }
        return "meetings/selectInstructor";
    }

    @GetMapping("/new/{instructorId}")
    public String selectService(@PathVariable("instructorId") int instructorId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser.hasRole("ROLE_STUDENT_UNDERGRAD")) {
            model.addAttribute("courses", courseService.getCoursesForUndergradStudentsByInstructorId(instructorId));
        } else if (currentUser.hasRole("ROLE_STUDENT_GRAD")) {
            model.addAttribute("courses", courseService.getCoursesForGradStudentByInstructorId(instructorId));
        }
        model.addAttribute(instructorId);
        return "meetings/selectService";
    }

    @GetMapping("/new/{instructorId}/{courseId}")
    public String selectDate(@PathVariable("courseId") int courseId, @PathVariable("instructorId") int instructorId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (courseService.isCourseForStudent(courseId, currentUser.getId())) {
            model.addAttribute(instructorId);
            model.addAttribute("workId", courseId);
            return "meetings/selectDate";
        } else {
            return "redirect:/meetings/new";
        }

    }

    @GetMapping("/new/{instructorId}/{courseId}/{dateTime}")
    public String showNewMeetingSummary(@PathVariable("courseId") int courseId, @PathVariable("instructorId") int instructorId, @PathVariable("dateTime") String start, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (meetingService.isAvailable(courseId, instructorId, currentUser.getId(), LocalDateTime.parse(start))) {
            model.addAttribute("course", courseService.getCourseById(courseId));
            model.addAttribute("instructor", userService.getInstructorById(instructorId).getFirstName() + " " + userService.getInstructorById(instructorId).getLastName());
            model.addAttribute(instructorId);
            model.addAttribute("start", LocalDateTime.parse(start));
            model.addAttribute("end", LocalDateTime.parse(start).plusMinutes(courseService.getCourseById(courseId).getDuration()));
            return "meetings/newMeetingSummary";
        } else {
            return "redirect:/meetings/new";
        }
    }

    @PostMapping("/new")
    public String bookMeeting(@RequestParam("courseId") int courseId, @RequestParam("instructorId") int instructorId, @RequestParam("start") String start, @AuthenticationPrincipal CustomUserDetails currentUser) {
        meetingService.createNewMeeting(courseId, instructorId, currentUser.getId(), LocalDateTime.parse(start));
        return "redirect:/meetings/all";
    }

    @PostMapping("/cancel")
    public String cancelMeeting(@RequestParam("meetingId") int meetingId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        meetingService.cancelUserMeetingById(meetingId, currentUser.getId());
        return "redirect:/meetings/all";
    }


    public static String formatDuration(Duration duration) {
        long s = duration.getSeconds();
        return String.format("%dh%02dm", s / 3600, (s % 3600) / 60);
    }

}
