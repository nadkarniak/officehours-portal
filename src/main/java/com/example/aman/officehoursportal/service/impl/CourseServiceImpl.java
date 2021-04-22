package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.CourseRepository;
import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.exception.CourseNotFoundException;
import com.example.aman.officehoursportal.service.UserService;
import com.example.aman.officehoursportal.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void createNewCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCourse(Course courseUpdateData) {
        Course course = getCourseById(courseUpdateData.getId());
        course.setName(courseUpdateData.getName());
        course.setCredits(courseUpdateData.getCredits());
        course.setDuration(courseUpdateData.getDuration());
        course.setDescription(courseUpdateData.getDescription());
        course.setEditable(courseUpdateData.getEditable());
        course.setTargetStudent(courseUpdateData.getTargetStudent());
        courseRepository.save(course);
    }

    @Override
    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourseById(int courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public boolean isCourseForStudent(int courseId, int studentId) {
        Student student = userService.getStudentById(studentId);
        Course course = getCourseById(courseId);
        if (student.hasRole("ROLE_STUDENT_UNDERGRAD") && !course.getTargetStudent().equals("undergrad")) {
            return false;
        } else return !student.hasRole("ROLE_STUDENT_GRAD") || course.getTargetStudent().equals("grad");
    }

    @Override
    public List<Course> getCoursesByInstructorId(int instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<Course> getUndergradStudentCourses() {
        return courseRepository.findByTargetStudent("undergrad");
    }

    @Override
    public List<Course> getGradStudentCourses() {
        return courseRepository.findByTargetStudent("grad");
    }

    @Override
    public List<Course> getCoursesForUndergradStudentsByInstructorId(int instructorId) {
        return courseRepository.findByTargetStudentAndInstructorId("undergrad", instructorId);
    }

    @Override
    public List<Course> getCoursesForGradStudentByInstructorId(int instructorId) {
        return courseRepository.findByTargetStudentAndInstructorId("grad", instructorId);
    }


}
