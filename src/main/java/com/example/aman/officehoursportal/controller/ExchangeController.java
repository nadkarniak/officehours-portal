package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.ExchangeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final MeetingService meetingService;

    public ExchangeController(ExchangeService exchangeService, MeetingService meetingService) {
        this.exchangeService = exchangeService;
        this.meetingService = meetingService;
    }

    @GetMapping("/{oldMeetingId}")
    public String showEligibleMeetingsToExchange(@PathVariable("oldMeetingId") int oldMeetingId, Model model) {
        List<Meeting> eligibleMeetings = exchangeService.getEligibleMeetingsForExchange(oldMeetingId);
        model.addAttribute("meetingId", oldMeetingId);
        model.addAttribute("numberOfEligibleMeetings", eligibleMeetings.size());
        model.addAttribute("eligibleMeetings", eligibleMeetings);
        return "exchange/listProposals";
    }

    @GetMapping("/{oldMeetingId}/{newMeetingId}")
    public String showExchangeSummaryScreen(@PathVariable("oldMeetingId") int oldMeetingId, @PathVariable("newMeetingId") int newMeetingId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (exchangeService.checkIfExchangeIsPossible(oldMeetingId, newMeetingId, currentUser.getId())) {
            model.addAttribute("oldMeeting", meetingService.getMeetingByIdWithAuthorization(oldMeetingId));
            model.addAttribute("newAppointment", meetingService.getMeetingById(newMeetingId));
        } else {
            return "redirect:/meetings/all";
        }

        return "exchange/exchangeSummary";
    }

    @PostMapping()
    public String processExchangeRequest(@RequestParam("oldMeetingId") int oldMeetingId, @RequestParam("newMeetingId") int newMeetingId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        boolean result = exchangeService.requestExchange(oldMeetingId, newMeetingId, currentUser.getId());
        if (result) {
            model.addAttribute("message", "Exchange request successfully sent!");
        } else {
            model.addAttribute("message", "Error! Exchange not sent!");
        }
        return "exchange/requestConfirmation";
    }

    @PostMapping("/accept")
    public String processExchangeAcceptation(@RequestParam("exchangeId") int exchangeId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        exchangeService.acceptExchange(exchangeId, currentUser.getId());
        return "redirect:/meetings/all";
    }

    @PostMapping("/reject")
    public String processExchangeRejection(@RequestParam("exchangeId") int exchangeId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        exchangeService.rejectExchange(exchangeId, currentUser.getId());
        return "redirect:/meetings/all";
    }
}
