package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Meeting;

import java.util.List;

public interface ExchangeService {

    boolean checkIfEligibleForExchange(int userId, int meetingId);

    List<Meeting> getEligibleMeetingsForExchange(int meetingId);

    boolean checkIfExchangeIsPossible(int oldMeetingId, int newMeetingId, int userId);

    boolean acceptExchange(int exchangeId, int userId);

    boolean rejectExchange(int exchangeId, int userId);

    boolean requestExchange(int oldMeetingId, int newMeetingId, int userId);
}
