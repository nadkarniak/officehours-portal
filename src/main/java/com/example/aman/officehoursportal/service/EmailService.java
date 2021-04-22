package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.ChatMessage;
import com.example.aman.officehoursportal.entity.ExchangeRequest;
import com.example.aman.officehoursportal.entity.Report;
import org.thymeleaf.context.Context;

import java.io.File;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, Context templateContext, File attachment);

    void sendMeetingFinishedNotification(Meeting meeting);

    void sendMeetingRejectionRequestedNotification(Meeting meeting);

    void sendNewMeetingScheduledNotification(Meeting meeting);

    void sendMeetingCanceledByStudentNotification(Meeting meeting);

    void sendMeetingCanceledByInstructorNotification(Meeting meeting);

    void sendReport(Report report);

    void sendMeetingRejectionAcceptedNotification(Meeting meeting);

    void sendNewChatMessageNotification(ChatMessage meeting);

    void sendNewExchangeRequestedNotification(Meeting oldMeeting, Meeting newMeeting);

    void sendExchangeRequestAcceptedNotification(ExchangeRequest exchangeRequest);

    void sendExchangeRequestRejectedNotification(ExchangeRequest exchangeRequest);
}
