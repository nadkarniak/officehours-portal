package com.example.aman.officehoursportal.security;

import com.example.aman.officehoursportal.service.MeetingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MeetingService meetingService;

    public CustomAuthenticationSuccessHandler(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        if (currentUser.hasRole("ROLE_ADMIN")) {
            meetingService.updateAllMeetingsStatuses();
        } else {
            meetingService.updateUserMeetingsStatuses(currentUser.getId());
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

}
