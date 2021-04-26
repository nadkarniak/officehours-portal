package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.WorkingPlan;
import com.example.aman.officehoursportal.model.ChangePasswordForm;
import com.example.aman.officehoursportal.model.TimePeroid;
import com.example.aman.officehoursportal.model.UserForm;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.UserService;
import com.example.aman.officehoursportal.service.CourseService;
import com.example.aman.officehoursportal.service.WorkingPlanService;
import com.example.aman.officehoursportal.validation.groups.CreateInstructor;
import com.example.aman.officehoursportal.validation.groups.CreateUser;
import com.example.aman.officehoursportal.validation.groups.UpdateInstructor;
import com.example.aman.officehoursportal.validation.groups.UpdateUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/instructors")

public class InstructorController {

    private final UserService userService;
    private final CourseService courseService;
    private final WorkingPlanService workingPlanService;
    private final MeetingService meetingService;

    public InstructorController(UserService userService, CourseService courseService, WorkingPlanService workingPlanService, MeetingService meetingService) {
        this.userService = userService;
        this.courseService = courseService;
        this.workingPlanService = workingPlanService;
        this.meetingService = meetingService;
    }


    @GetMapping("/all")
    public String showAllInstructors(Model model) {
        model.addAttribute("instructors", userService.getAllInstructors());
        return "users/listInstructors";
    }

    @GetMapping("/{id}")
    public String showInstructorDetails(@PathVariable("id") int instructorId, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser.getId() == instructorId || currentUser.hasRole("ROLE_ADMIN")) {
            if (!model.containsAttribute("user")) {
                model.addAttribute("user", new UserForm(userService.getInstructorById(instructorId)));
            }
            if (!model.containsAttribute("passwordChange")) {
                model.addAttribute("passwordChange", new ChangePasswordForm(instructorId));
            }
            model.addAttribute("account_type", "instructor");
            model.addAttribute("formActionProfile", "/instructors/update/profile");
            model.addAttribute("formActionPassword", "/instructors/update/password");
            model.addAttribute("allCourses", courseService.getAllCourses());
            model.addAttribute("numberOfScheduledMeetings", meetingService.getNumberOfScheduledMeetingsForUser(instructorId));
            model.addAttribute("numberOfCanceledMeetings", meetingService.getNumberOfCanceledMeetingsForUser(instructorId));
            return "users/updateUserForm";

        } else {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
    }

    @PostMapping("/update/profile")
    public String processInstructorUpdate(@Validated({UpdateUser.class, UpdateInstructor.class}) @ModelAttribute("user") UserForm userUpdateData, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", userUpdateData);
            return "redirect:/instructors/" + userUpdateData.getId();
        }
        userService.updateInstructorProfile(userUpdateData);
        return "redirect:/instructors/" + userUpdateData.getId();
    }

    @GetMapping("/new")
    public String showInstructorRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) model.addAttribute("user", new UserForm());
        model.addAttribute("account_type", "instructor");
        model.addAttribute("registerAction", "/instructors/new");
        model.addAttribute("allCourses", courseService.getAllCourses());
        return "users/createUserForm";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String processInstructorRegistrationForm(@Validated({CreateUser.class, CreateInstructor.class}) @ModelAttribute("user") UserForm userForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", userForm);
            return "redirect:/instructors/new";
        }
        userService.saveNewInstructor(userForm);
        return "redirect:/instructors/all";
    }

    @PostMapping("/delete")
    public String processDeleteInstructorRequest(@RequestParam("instructorId") int instructorId) {
        userService.deleteUserById(instructorId);
        return "redirect:/instructors/all";
    }

    @GetMapping("/availability")
    public String showInstructorAvailability(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        model.addAttribute("plan", workingPlanService.getWorkingPlanByInstructorId(currentUser.getId()));
        model.addAttribute("breakModel", new TimePeroid());
        return "users/showOrUpdateInstructorAvailability";
    }

    @PostMapping("/availability")
    public String processInstructorWorkingPlanUpdate(@ModelAttribute("plan") WorkingPlan plan) {
        workingPlanService.updateWorkingPlan(plan);
        return "redirect:/instructors/availability";
    }

    @PostMapping("/availability/breakes/add")
    public String processInstructorAddBreak(@ModelAttribute("plants") TimePeroid breakToAdd, @RequestParam("planId") int planId, @RequestParam("dayOfWeek") String dayOfWeek) {
        workingPlanService.addBreakToWorkingPlan(breakToAdd, planId, dayOfWeek);
        return "redirect:/instructors/availability";
    }

    @PostMapping("/availability/breakes/delete")
    public String processInstructorsDeleteBreak(@ModelAttribute("breakModel") TimePeroid breakToDelete, @RequestParam("planId") int planId, @RequestParam("dayOfWeek") String dayOfWeek) {
        workingPlanService.deleteBreakFromWorkingPlan(breakToDelete, planId, dayOfWeek);
        return "redirect:/instructors/availability";
    }

    @PostMapping("/update/password")
    public String processInstructorPasswordUpate(@Valid @ModelAttribute("passwordChange") ChangePasswordForm passwordChange, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChange", bindingResult);
            redirectAttributes.addFlashAttribute("passwordChange", passwordChange);
            return "redirect:/instructors/" + passwordChange.getId();
        }
        userService.updateUserPassword(passwordChange);
        return "redirect:/instructors/" + passwordChange.getId();
    }


}
