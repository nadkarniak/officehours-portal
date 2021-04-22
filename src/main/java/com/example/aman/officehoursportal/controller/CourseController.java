package com.example.aman.officehoursportal.controller;

import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/all")
    public String showAllCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }

    @GetMapping("/{courseId}")
    public String showFormForUpdateCourse(@PathVariable("courseId") int courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        return "courses/createOrUpdateCourseForm";
    }

    @GetMapping("/new")
    public String showFormForAddCourse(Model model) {
        model.addAttribute("course", new Course());
        return "courses/createOrUpdateCourseForm";
    }

    @PostMapping("/new")
    public String saveCourses(@ModelAttribute("course") Course course) {
        if (course.getId() != null) {
            courseService.updateCourse(course);
        } else {
            courseService.createNewCourse(course);
        }
        return "redirect:/courses/all";
    }

    @PostMapping("/delete")
    public String deleteCourse(@RequestParam("courseId") int courseId) {
        courseService.deleteCourseById(courseId);
        return "redirect:/courses/all";
    }
}
