package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.ChatMessage;
import com.example.aman.officehoursportal.entity.ExchangeRequest;
import com.example.aman.officehoursportal.entity.Report;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.service.EmailService;
import com.example.aman.officehoursportal.util.PdfGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JwtTokenServiceImpl jwtTokenService;
    private final PdfGeneratorUtil pdfGenaratorUtil;
    private final String baseUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, JwtTokenServiceImpl jwtTokenService, PdfGeneratorUtil pdfGenaratorUtil, @Value("${base.url}") String baseUrl) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jwtTokenService = jwtTokenService;
        this.pdfGenaratorUtil = pdfGenaratorUtil;
        this.baseUrl = baseUrl;
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String templateName, Context templateContext, File attachment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String html = templateEngine.process("email/" + templateName, templateContext);

            helper.setTo(to);
            helper.setFrom("johntokahto@gmail.com");
            helper.setSubject(subject);
            helper.setText(html, true);

            if (attachment != null) {
                helper.addAttachment("report", attachment);
            }

            javaMailSender.send(message);

        } catch (MessagingException e) {
            log.error("Error while adding attachment to email, error is {}", e.getLocalizedMessage());
        }

    }

    @Async
    @Override
    public void sendMeetingFinishedNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        context.setVariable("url", baseUrl + "/meetings/reject?token=" + jwtTokenService.generateMeetingRejectionToken(meeting));
        sendEmail(meeting.getStudent().getEmail(), "Finished meeting summary", "meetingFinished", context, null);
    }

    @Async
    @Override
    public void sendMeetingRejectionRequestedNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        context.setVariable("url", baseUrl + "/meetings/acceptRejection?token=" + jwtTokenService.generateAcceptRejectionToken(meeting));
        sendEmail(meeting.getInstructor().getEmail(), "Rejection requested", "meetingRejectionRequested", context, null);
    }

    @Async
    @Override
    public void sendNewMeetingScheduledNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        sendEmail(meeting.getInstructor().getEmail(), "New meeting booked", "newMeetingScheduled", context, null);
    }

    @Async
    @Override
    public void sendMeetingCanceledByStudentNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        context.setVariable("canceler", "student");
        sendEmail(meeting.getInstructor().getEmail(), "Meeting canceled by Student", "appointmentCanceled", context, null);
    }

    @Async
    @Override
    public void sendMeetingCanceledByInstructorNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        context.setVariable("canceler", "instructor");
        sendEmail(meeting.getStudent().getEmail(), "Meeting canceled by Instructor", "meetingCanceled", context, null);
    }

    @Async
    @Override
    public void sendReport(Report report) {
        Context context = new Context();
        context.setVariable("student", report.getMeetings().get(0).getStudent().getFirstName() + " " + report.getMeetings().get(0).getStudent().getLastName());
        try {
            File invoicePdf = pdfGenaratorUtil.generatePdfFromReport(report);
            sendEmail(report.getMeetings().get(0).getStudent().getEmail(), "Meeting report", "meetingReport", context, invoicePdf);
        } catch (Exception e) {
            log.error("Error while generating pdf, error is {}", e.getLocalizedMessage());
        }

    }

    @Async
    @Override
    public void sendMeetingRejectionAcceptedNotification(Meeting meeting) {
        Context context = new Context();
        context.setVariable("meeting", meeting);
        sendEmail(meeting.getStudent().getEmail(), "Rejection request accepted", "meetingRejectionAccepted", context, null);
    }

    @Async
    @Override
    public void sendNewChatMessageNotification(ChatMessage chatMessage) {
        Context context = new Context();
        User recipient = chatMessage.getAuthor() == chatMessage.getMeeting().getInstructor() ? chatMessage.getMeeting().getStudent() : chatMessage.getMeeting().getInstructor();
        context.setVariable("recipient", recipient);
        context.setVariable("meeting", chatMessage.getMeeting());
        context.setVariable("url", baseUrl + "/meetings/" + chatMessage.getMeeting().getId());
        sendEmail(recipient.getEmail(), "New chat message", "newChatMessage", context, null);
    }

    @Async
    @Override
    public void sendNewExchangeRequestedNotification(Meeting oldMeeting, Meeting newMeeting) {
        Context context = new Context();
        context.setVariable("oldMeeting", oldMeeting);
        context.setVariable("newMeeting", newMeeting);
        context.setVariable("url", baseUrl + "/meetings/" + newMeeting.getId());
        sendEmail(newMeeting.getStudent().getEmail(), "New Meeting Exchange Request", "newExchangeRequest", context, null);
    }

    @Override
    public void sendExchangeRequestAcceptedNotification(ExchangeRequest exchangeRequest) {
        Context context = new Context();
        context.setVariable("exchangeRequest", exchangeRequest);
        context.setVariable("url", baseUrl + "/meetings/" + exchangeRequest.getRequested().getId());
        sendEmail(exchangeRequest.getRequested().getStudent().getEmail(), "Exchange request accepted", "exchangeRequestAccepted", context, null);
    }

    @Override
    public void sendExchangeRequestRejectedNotification(ExchangeRequest exchangeRequest) {
        Context context = new Context();
        context.setVariable("exchangeRequest", exchangeRequest);
        context.setVariable("url", baseUrl + "/meetings/" + exchangeRequest.getRequestor().getId());
        sendEmail(exchangeRequest.getRequestor().getStudent().getEmail(), "Exchange request rejected", "exchangeRequestRejected", context, null);
    }
}
