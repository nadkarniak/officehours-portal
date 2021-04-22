package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.ReportRepository;
import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.MeetingStatus;
import com.example.aman.officehoursportal.entity.Report;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.ReportService;
import com.example.aman.officehoursportal.service.NotificationService;
import com.example.aman.officehoursportal.service.UserService;
import com.example.aman.officehoursportal.util.PdfGeneratorUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PdfGeneratorUtil pdfGeneratorUtil;
    private final UserService userService;
    private final MeetingService meetingService;
    private final NotificationService notificationService;

    public ReportServiceImpl(ReportRepository reportRepository, PdfGeneratorUtil pdfGeneratorUtil, UserService userService, MeetingService meetingService, NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.pdfGeneratorUtil = pdfGeneratorUtil;
        this.userService = userService;
        this.meetingService = meetingService;
        this.notificationService = notificationService;
    }

    @Override
    public String generateReportNumber() {
        List<Report> reports = reportRepository.findAllIssuedInCurrentMonth(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
        int nextReportNumber = reports.size() + 1;
        LocalDateTime today = LocalDateTime.now();
        return "FV/" + today.getYear() + "/" + today.getMonthValue() + "/" + nextReportNumber;
    }

    @Override
    public void createNewReport(Report report) {
        reportRepository.save(report);
    }

    @Override
    public Report getReportByMeetingId(int meetingId) {
        return reportRepository.findByMeetingId(meetingId);
    }

    @Override
    public Report getReportById(int reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public File generatePdfForReport(int reportId) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Report report = reportRepository.getOne(reportId);
        if (!isUserAllowedToDownloadReport(currentUser, report)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        return pdfGeneratorUtil.generatePdfFromReport(report);
    }

    @Override
    public boolean isUserAllowedToDownloadReport(CustomUserDetails user, Report report) {
        int userId = user.getId();
        if (user.hasRole("ROLE_ADMIN")) {
            return true;
        }
        for (Meeting a : report.getMeetings()) {
            if (a.getInstructor().getId() == userId || a.getStudent().getId() == userId) {
                return true;
            }
        }
        return false;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void changeReportStatusToRead(int reportId) {
        Report report = reportRepository.getOne(reportId);
        report.setStatus("read");
        reportRepository.save(report);
    }

    @Transactional
    @Override
    public void issueReportsForConfirmedMeetings() {
        List<Student> students = userService.getAllStudents();
        for (Student student : students) {
            List<Meeting> meetingsToIssueReport = meetingService.getConfirmedMeetingByStudentId(student.getId());
            if (!meetingsToIssueReport.isEmpty()) {
                for (Meeting a : meetingsToIssueReport) {
                    a.setStatus(MeetingStatus.READED);
                    meetingService.updateMeeting(a);
                }
                Report report = new Report(generateReportNumber(), "issued", LocalDateTime.now(), meetingsToIssueReport);
                reportRepository.save(report);
                notificationService.newReport(report, true);
            }

        }
    }
}
