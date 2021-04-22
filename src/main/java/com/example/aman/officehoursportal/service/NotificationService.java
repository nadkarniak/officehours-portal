package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.*;
import com.example.aman.officehoursportal.entity.user.User;

import java.util.List;

public interface NotificationService {

    void newNotification(String title, String message, String url, User user);

    void markAsRead(int notificationId, int userId);

    void markAllAsRead(int userId);

    Notification getNotificationById(int notificationId);

    List<Notification> getAll(int userId);

    List<Notification> getUnreadNotifications(int userId);

    void newMeetingFinishedNotification(Meeting meeting, boolean sendEmail);

    void newMeetingRejectionRequestedNotification(Meeting meeting, boolean sendEmail);

    void newNewMeetingScheduledNotification(Meeting meeting, boolean sendEmail);

    void newMeetingCanceledByStudentNotification(Meeting meeting, boolean sendEmail);

    void newMeetingCanceledByInstructorNotification(Meeting meeting, boolean sendEmail);

    void newMeetingRejectionAcceptedNotification(Meeting meeting, boolean sendEmail);

    void newChatMessageNotification(ChatMessage chatMessage, boolean sendEmail);

    void newReport(Report report, boolean sendEmail);

    void newExchangeRequestedNotification(Meeting oldMeeting, Meeting newMeeting, boolean sendEmail);

    void newExchangeAcceptedNotification(ExchangeRequest exchangeRequest, boolean sendEmail);

    void newExchangeRejectedNotification(ExchangeRequest exchangeRequest, boolean sendEmail);
}
