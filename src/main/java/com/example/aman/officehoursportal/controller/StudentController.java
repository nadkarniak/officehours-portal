package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.model.ChangePasswordForm;
import com.example.aman.officehoursportal.model.UserForm;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.MeetingService;
import com.example.aman.officehoursportal.service.UserService;
import com.example.aman.officehoursportal.validation.groups.CreateGraduateStudent;
import com.example.aman.officehoursportal.validation.groups.CreateUser;
import com.example.aman.officehoursportal.validation.groups.UpdateGraduateStudent;
import com.example.aman.officehoursportal.validation.groups.UpdateUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/students")
public class StudentController {
    private final UserService userService;
    private final MeetingService meetingService;

    public StudentController(UserService userService, MeetingService meetingService) {
        this.userService = userService;
        this.meetingService = meetingService;
    }

    @GetMapping("/all")
    public String showAllStudents(Model model) {
        model.addAttribute("students", userService.getAllStudents());
        return "users/listStudents";
    }

    @GetMapping("/{id}")
    public String showStudentDetails(@PathVariable int id, Model model) {
        Student student = userService.getStudentById(id);
        if (student.hasRole("ROLE_STUDENT_GRAD")) {
            if (!model.containsAttribute("user")) {
                model.addAttribute("user", new UserForm(userService.getGradStudentById(id)));
            }
            model.addAttribute("account_type", "student_grad");
            model.addAttribute("formActionProfile", "/students/grad/update/profile");
        } else if (student.hasRole("ROLE_STUDENT_UNDERGRAD")) {
            if (!model.containsAttribute("user")) {
                model.addAttribute("user", new UserForm(userService.getUndergradStudentById(id)));
            }
            model.addAttribute("account_type", "student_undergrad");
            model.addAttribute("formActionProfile", "/students/undergrad/update/profile");
        }
        if (!model.containsAttribute("passwordChange")) {
            model.addAttribute("passwordChange", new ChangePasswordForm(id));
        }
        model.addAttribute("formActionPassword", "/students/update/password");
        model.addAttribute("numberOfScheduledMeetings", meetingService.getNumberOfScheduledMeetingsForUser(id));
        model.addAttribute("numberOfCanceledMeetings", meetingService.getNumberOfCanceledMeetingsForUser(id));
        return "users/updateUserForm";
    }

    @PostMapping("/grad/update/profile")
    public String processGradStudentProfileUpdate(@Validated({UpdateUser.class, UpdateGraduateStudent.class}) @ModelAttribute("user") UserForm user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/students/" + user.getId();
        }
        userService.updateGradStudentProfile(user);
        return "redirect:/students/" + user.getId();
    }

    @PostMapping("/undergrad/update/profile")
    public String processUndergradStudentProfileUpdate(@Validated({UpdateUser.class}) @ModelAttribute("user") UserForm user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/students/" + user.getId();
        }
        userService.updateUndergradStudentProfile(user);
        return "redirect:/students/" + user.getId();
    }


    @RequestMapping(value="/new/{student_type}", method = {RequestMethod.GET,RequestMethod.POST})
    public String showStudentRegistrationForm(@PathVariable("student_type") String studentType, Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser!=null && !(currentUser.hasRole("ROLE_ADMIN"))) {
            return "redirect:/";
        }
        if (studentType.equals("grad")) {
            model.addAttribute("account_type", "student_grad");
            model.addAttribute("action", "/students/new/grad");
        } else if (studentType.equals("undergrad")) {
            model.addAttribute("account_type", "student_undergrad");
            model.addAttribute("action", "/students/new/undergrad");
        } else {
            throw new RuntimeException();
        }
        model.addAttribute("user", new UserForm());
        return "users/createUserForm";
    }


    @PostMapping("/new/undergrad")
    public String processUndergradStudentRegistration(@Validated({CreateUser.class}) @ModelAttribute("user") UserForm userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model, userForm, "student_undergrad", "/students/new/undergrad", null);
            return "users/createUserForm";
        }
        userService.saveNewUndergradStudent(userForm);
        model.addAttribute("createdUserName", userForm.getUserName());
        return "users/successful";
    }

    @PostMapping("/new/grad")
    public String processGradStudentRegistration(@Validated({CreateUser.class, CreateGraduateStudent.class}) @ModelAttribute("user") UserForm userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model, userForm, "student_grad", "/students/new/grad", null);
            return "users/createUserForm";
        }
        userService.saveNewGradStudent(userForm);
        model.addAttribute("createdUserName", userForm.getUserName());
        return "users/successful";
    }


    @PostMapping("/update/password")
    public String processStudentPasswordUpdate(@Valid @ModelAttribute("passwordChange") ChangePasswordForm passwordChange, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails currentUser, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChange", bindingResult);
            redirectAttributes.addFlashAttribute("passwordChange", passwordChange);
            return "redirect:/students/" + currentUser.getId();
        }
        userService.updateUserPassword(passwordChange);
        return "redirect:/students/" + currentUser.getId();
    }

    @PostMapping("/delete")
    public String processDeleteStudentRequest(@RequestParam("studentId") int studentId) {
        userService.deleteUserById(studentId);
        return "redirect:/students/all";
    }

    public Model populateModel(Model model, UserForm user, String account_type, String action, String error) {
        model.addAttribute("user", user);
        model.addAttribute("account_type", account_type);
        model.addAttribute("registerAction", action);
        model.addAttribute("registrationError", error);
        return model;
    }

}
