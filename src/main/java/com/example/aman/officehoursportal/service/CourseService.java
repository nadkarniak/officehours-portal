package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.Course;

import java.util.List;

public interface CourseService {
    void createNewCourse(Course course);

    Course getCourseById(int courseId);

    List<Course> getAllCourses();

    List<Course> getCoursesByInstructorId(int instructorId);

    List<Course> getUndergradStudentCourses();

    List<Course> getGradStudentCourses();

    List<Course> getCoursesForUndergradStudentsByInstructorId(int instructorId);

    List<Course> getCoursesForGradStudentByInstructorId(int instructorId);

    void updateCourse(Course course);

    void deleteCourseById(int courseId);

    boolean isCourseForStudent(int courseId, int studentId);
}
