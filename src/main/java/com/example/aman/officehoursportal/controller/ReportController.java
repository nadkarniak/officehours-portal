package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public String showAllReports(Model model) {
        model.addAttribute("invoices", reportService.getAllReports());
        return "reports/listReports";
    }

    @PostMapping("/read/{reportId}")
    public String changeStatusToRead(@PathVariable("reportId") int reportId) {
        reportService.changeReportStatusToRead(reportId);
        return "redirect:/reports/all";
    }

    @GetMapping("/issue")
    public String issueReportsManually(Model model) {
        reportService.issueReportsForConfirmedMeetings();
        return "redirect:/reports/all";
    }

    @GetMapping("/download/{reportId}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable("reportId") int reportId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            File invoicePdf = reportService.generatePdfForReport(reportId);
            HttpHeaders respHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/pdf");
            respHeaders.setContentType(mediaType);
            respHeaders.setContentLength(invoicePdf.length());
            respHeaders.setContentDispositionFormData("attachment", invoicePdf.getName());
            InputStreamResource isr = new InputStreamResource(new FileInputStream(invoicePdf));
            return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            log.error("Error while generating pdf for download, error: {} ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
