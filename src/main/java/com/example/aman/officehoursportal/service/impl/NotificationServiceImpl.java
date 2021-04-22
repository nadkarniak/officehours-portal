package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.NotificationRepository;
import com.example.aman.officehoursportal.entity.*;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.service.EmailService;
import com.example.aman.officehoursportal.service.NotificationService;
import com.example.aman.officehoursportal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final boolean mailingEnabled;

    public NotificationServiceImpl(@Value("${mailing.enabled}") boolean mailingEnabled, NotificationRepository notificationRepository, UserService userService, EmailService emailService) {
        this.mailingEnabled = mailingEnabled;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void newNotification(String title, String message, String url, User user) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setUrl(url);
        notification.setCreatedAt(new Date());
        notification.setMessage(message);
        notification.setUser(user);
        notificationRepository.save(notification);
    }


    @Override
    public void markAsRead(int notificationId, int userId) {
        Notification notification = notificationRepository.getOne(notificationId);
        if (notification.getUser().getId() == userId) {
            notification.setRead(true);
            notificationRepository.save(notification);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
    }

    @Override
    public void markAllAsRead(int userId) {
        List<Notification> notifications = notificationRepository.getAllUnreadNotifications(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public Notification getNotificationById(int notificationId) {
        return notificationRepository.getOne(notificationId);
    }

    @Override
    public List<Notification> getAll(int userId) {
        return userService.getUserById(userId).getNotifications();
    }

    @Override
    public List<Notification> getUnreadNotifications(int userId) {
        return notificationRepository.getAllUnreadNotifications(userId);
    }

    @Override
    public void newMeetingFinishedNotification(Meeting meeting, boolean sendEmail) {
        String title = "Meeting Finished";
        String message = "Meeting finished, you can reject that it took place until " + meeting.getEnd().plusHours(24).toString();
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendMeetingFinishedNotification(meeting);
        }

    }

    @Override
    public void newMeetingRejectionRequestedNotification(Meeting meeting, boolean sendEmail) {
        String title = "Meeting Rejected";
        String message = meeting.getStudent().getFirstName() + " " + meeting.getStudent().getLastName() + "rejected an appointment. Your approval is required";
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getInstructor());
        if (sendEmail && mailingEnabled) {
            emailService.sendMeetingRejectionRequestedNotification(meeting);
        }
    }

    @Override
    public void newNewMeetingScheduledNotification(Meeting meeting, boolean sendEmail) {
        String title = "New meeting scheduled";
        String message = "New meeting scheduled with" + meeting.getStudent().getFirstName() + " " + meeting.getInstructor().getLastName() + " on " + meeting.getStart().toString();
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getInstructor());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewMeetingScheduledNotification(meeting);
        }
    }

    @Override
    public void newMeetingCanceledByStudentNotification(Meeting meeting, boolean sendEmail) {
        String title = "Meeting Canceled";
        String message = meeting.getStudent().getFirstName() + " " + meeting.getStudent().getLastName() + " cancelled meeting scheduled at " + meeting.getStart().toString();
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getInstructor());
        if (sendEmail && mailingEnabled) {
            emailService.sendMeetingCanceledByStudentNotification(meeting);
        }
    }

    @Override
    public void newMeetingCanceledByInstructorNotification(Meeting meeting, boolean sendEmail) {
        String title = "Meeting Canceled";
        String message = meeting.getInstructor().getFirstName() + " " + meeting.getInstructor().getLastName() + " cancelled meeting scheduled at " + meeting.getStart().toString();
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendMeetingCanceledByInstructorNotification(meeting);
        }
    }

    public void newReport(Report report, boolean sendEmail) {
        String title = "New report";
        String message = "New report has been issued for you";
        String url = "/reports/" + report.getId();
        newNotification(title, message, url, report.getMeetings().get(0).getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendReport(report);
        }
    }

    @Override
    public void newExchangeRequestedNotification(Meeting oldMeeting, Meeting newMeeting, boolean sendEmail) {
        String title = "Request for exchange";
        String message = "One of the users sent you a request to exchange his meeting with your meeting";
        String url = "/meetings/" + newMeeting.getId();
        newNotification(title, message, url, newMeeting.getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewExchangeRequestedNotification(oldMeeting, newMeeting);
        }
    }

    @Override
    public void newExchangeAcceptedNotification(ExchangeRequest exchangeRequest, boolean sendEmail) {
        String title = "Exchange request accepted";
        String message = "Someone accepted your appointment exchange request from " + exchangeRequest.getRequested().getStart() + " to " + exchangeRequest.getRequestor().getStart();
        String url = "/appointments/" + exchangeRequest.getRequested();
        newNotification(title, message, url, exchangeRequest.getRequested().getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendExchangeRequestAcceptedNotification(exchangeRequest);
        }
    }

    @Override
    public void newExchangeRejectedNotification(ExchangeRequest exchangeRequest, boolean sendEmail) {
        String title = "Exchange request rejected";
        String message = "Someone rejected your appointment exchange request from " + exchangeRequest.getRequestor().getStart() + " to " + exchangeRequest.getRequested().getStart();
        String url = "/appointments/" + exchangeRequest.getRequestor();
        newNotification(title, message, url, exchangeRequest.getRequestor().getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendExchangeRequestRejectedNotification(exchangeRequest);
        }
    }

    @Override
    public void newMeetingRejectionAcceptedNotification(Meeting meeting, boolean sendEmail) {
        String title = "Rejection accepted";
        String message = "You instructor accepted your rejection request";
        String url = "/meetings/" + meeting.getId();
        newNotification(title, message, url, meeting.getStudent());
        if (sendEmail && mailingEnabled) {
            emailService.sendMeetingRejectionAcceptedNotification(meeting);
        }
    }

    @Override
    public void newChatMessageNotification(ChatMessage chatMessage, boolean sendEmail) {
        String title = "New chat message";
        String message = "You have new chat message from " + chatMessage.getAuthor().getFirstName() + " regarding meeting scheduled at " + chatMessage.getMeeting().getStart();
        String url = "/meetings/" + chatMessage.getMeeting().getId();
        newNotification(title, message, url, chatMessage.getAuthor() == chatMessage.getMeeting().getInstructor() ? chatMessage.getMeeting().getStudent() : chatMessage.getMeeting().getInstructor());
        if (sendEmail && mailingEnabled) {
            emailService.sendNewChatMessageNotification(chatMessage);
        }
    }

}
