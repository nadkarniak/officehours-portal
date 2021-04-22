package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Report;
import com.example.aman.officehoursportal.security.CustomUserDetails;

import java.io.File;
import java.util.List;

public interface ReportService {
    void createNewReport(Report report);

    Report getReportByMeetingId(int meetingId);

    Report getReportById(int reportId);

    List<Report> getAllReports();

    void changeReportStatusToRead(int reportId);

    void issueReportsForConfirmedMeetings();

    String generateReportNumber();

    File generatePdfForReport(int reportId);

    boolean isUserAllowedToDownloadReport(CustomUserDetails user, Report report);
}

