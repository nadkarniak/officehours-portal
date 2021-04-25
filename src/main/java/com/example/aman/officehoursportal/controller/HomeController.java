package com.example.aman.officehoursportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import com.example.aman.officehoursportal.service.NotificationService;

import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showHome(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        model.addAttribute("user", userService.getUserById(currentUser.getId()));
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser != null) {
            return "redirect:/";
        }
        return "users/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "access-denied";
    }


}
