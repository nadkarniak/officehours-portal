package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.MeetingRepository;
import com.example.aman.officehoursportal.dao.ExchangeRequestRepository;
import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.MeetingStatus;
import com.example.aman.officehoursportal.entity.ExchangeRequest;
import com.example.aman.officehoursportal.entity.ExchangeStatus;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.service.ExchangeService;
import com.example.aman.officehoursportal.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;
    private final ExchangeRequestRepository exchangeRequestRepository;

    public ExchangeServiceImpl(MeetingRepository meetingRepository, NotificationService notificationService, ExchangeRequestRepository exchangeRequestRepository) {
        this.meetingRepository = meetingRepository;
        this.notificationService = notificationService;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    @Override
    public boolean checkIfEligibleForExchange(int userId, int meetingId) {
        Meeting meeting = meetingRepository.getOne(meetingId);
        return meeting.getStart().minusHours(24).isAfter(LocalDateTime.now()) && meeting.getStudent().equals(MeetingStatus.SCHEDULED) && meeting.getStudent().getId() == userId;
    }

    @Override
    public List<Meeting> getEligibleMeetingsForExchange(int meetingId) {
        Meeting meetingToExchange = meetingRepository.getOne(meetingId  );
        return meetingRepository.getEligibleMeetingsForExchange(LocalDateTime.now().plusHours(24), meetingToExchange.getStudent().getId(), meetingToExchange.getStudent().getId(), meetingToExchange.getCourse().getId());
    }

    @Override
    public boolean checkIfExchangeIsPossible(int oldMeetingId, int newMeetingId, int userId) {
        Meeting oldMeeting = meetingRepository.getOne(oldMeetingId);
        Meeting newMeeting = meetingRepository.getOne(newMeetingId);
        if (oldMeeting.getStudent().getId() == userId) {
            return oldMeeting.getCourse().getId().equals(newMeeting.getCourse().getId())
                    && oldMeeting.getInstructor().getId().equals(newMeeting.getInstructor().getId())
                    && oldMeeting.getStart().minusHours(24).isAfter(LocalDateTime.now())
                    && newMeeting.getStart().minusHours(24).isAfter(LocalDateTime.now());
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }

    }

    @Override
    public boolean acceptExchange(int exchangeId, int userId) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.getOne(exchangeId);
        Meeting requestor = exchangeRequest.getRequestor();
        Meeting requested = exchangeRequest.getRequested();
        Student tempStudent = requestor.getStudent();
        requestor.setStatus(MeetingStatus.SCHEDULED);
        exchangeRequest.setStatus(ExchangeStatus.ACCEPTED);
        requestor.setStudent(requested.getStudent());
        requested.setStudent(tempStudent);
        exchangeRequestRepository.save(exchangeRequest);
        meetingRepository.save(requested);
        meetingRepository.save(requestor);
        notificationService.newExchangeAcceptedNotification(exchangeRequest, true);
        return true;
    }

    @Override
    public boolean rejectExchange(int exchangeId, int userId) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.getOne(exchangeId);
        Meeting requestor = exchangeRequest.getRequestor();
        exchangeRequest.setStatus(ExchangeStatus.REJECTED);
        requestor.setStatus(MeetingStatus.SCHEDULED);
        exchangeRequestRepository.save(exchangeRequest);
        meetingRepository.save(requestor);
        notificationService.newExchangeRejectedNotification(exchangeRequest, true);
        return true;
    }

    @Override
    public boolean requestExchange(int oldMeetingId, int newMeetingId, int userId) {
        if (checkIfExchangeIsPossible(oldMeetingId, newMeetingId, userId)) {
            Meeting oldMeeting = meetingRepository.getOne(oldMeetingId);
            Meeting newMeeting = meetingRepository.getOne(newMeetingId);
            oldMeeting.setStatus(MeetingStatus.EXCHANGE_REQUESTED);
            meetingRepository.save(oldMeeting);
            ExchangeRequest exchangeRequest = new ExchangeRequest(oldMeeting, newMeeting, ExchangeStatus.PENDING);
            exchangeRequestRepository.save(exchangeRequest);
            notificationService.newExchangeRequestedNotification(oldMeeting, newMeeting, true);
            return true;
        }
        return false;
    }
}
