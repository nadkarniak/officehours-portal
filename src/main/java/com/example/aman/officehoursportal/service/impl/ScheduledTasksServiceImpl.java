package com.example.aman.officehoursportal.service.impl;


import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.ReportService;
import com.example.aman.officehoursportal.service.ScheduledTasksService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    private final MeetingService meetingService;
    private final ReportService reportService;

    public ScheduledTasksServiceImpl(MeetingService meetingService, ReportService reportService) {
        this.meetingService = meetingService;
        this.reportService = reportService;
    }

    // runs every 30 minutes
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    @Override
    public void updateAllMeetingsStatuses() {
        meetingService.updateMeetingsStatusesWithExpiredExchangeRequest();
        meetingService.updateAllMeetingsStatuses();
    }

    // runs on the first day of each month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Override
    public void issueReportsForCurrentMonth() {
        reportService.issueReportsForConfirmedMeetings();
    }


}
