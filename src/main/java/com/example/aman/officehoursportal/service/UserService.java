package com.example.aman.officehoursportal.service;


import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import com.example.aman.officehoursportal.entity.user.student.GradStudent;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.entity.user.student.UndergradStudent;
import com.example.aman.officehoursportal.model.ChangePasswordForm;
import com.example.aman.officehoursportal.model.UserForm;

import java.util.Collection;
import java.util.List;

public interface UserService {
    /*
     * User
     * */
    boolean userExists(String userName);
    User getUserById(int userId);
    User getUserByUsername(String userName);
    List<User> getUsersByRoleName(String roleName);
    List<User> getAllUsers();
    void deleteUserById(int userId);
    void updateUserPassword(ChangePasswordForm passwordChangeForm);

    /*
     * Instructors
     * */
    Instructor getInstructorById(int instructorId);
    List<Instructor> getInstructorsWithUndergradCourses();
    List<Instructor> getInstructorsWithGradCourses();
    List<Instructor> getInstructorsByCourse(Course course);
    List<Instructor> getAllInstructors();
    void saveNewInstructor(UserForm userForm);
    void updateInstructorProfile(UserForm updateData);
    Collection<Role> getRolesForInstructor();

    /*
     * Students
     * */
    Student getStudentById(int studentId);
    List<Student> getAllStudents(String keyword);
    /*
     * UndergradStudent
     * */
    UndergradStudent getUndergradStudentById(int undergradStudentId);
    void saveNewUndergradStudent(UserForm userForm);
    void updateUndergradStudentProfile(UserForm updateData);
    Collection<Role> getRolesForUndergradStudent();

    /*
     * Grad Student
     * */
    GradStudent getGradStudentById(int gradStudentId);
    List<UndergradStudent> getAllUndergradStudents();
    void saveNewGradStudent(UserForm userForm);
    void updateGradStudentProfile(UserForm updateData);
    Collection<Role> getRoleForGradStudents();


}

