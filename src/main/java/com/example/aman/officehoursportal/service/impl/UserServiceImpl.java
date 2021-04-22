package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.RoleRepository;
import com.example.aman.officehoursportal.dao.user.UserRepository;
import com.example.aman.officehoursportal.dao.user.student.StudentRepository;
import com.example.aman.officehoursportal.dao.user.instructor.InstructorRepository;
import com.example.aman.officehoursportal.dao.user.student.GradStudentRepository;
import com.example.aman.officehoursportal.dao.user.student.UndergradStudentRepository;
import com.example.aman.officehoursportal.entity.WorkingPlan;
import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import com.example.aman.officehoursportal.entity.user.student.GradStudent;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.entity.user.student.UndergradStudent;
import com.example.aman.officehoursportal.model.ChangePasswordForm;
import com.example.aman.officehoursportal.model.UserForm;
import com.example.aman.officehoursportal.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final GradStudentRepository gradStudentRepository;
    private final UndergradStudentRepository undergradStudentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(InstructorRepository instructorRepository, StudentRepository studentRepository, GradStudentRepository gradStudentRepository, UndergradStudentRepository undergradStudentRepository, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.gradStudentRepository = gradStudentRepository;
        this.undergradStudentRepository = undergradStudentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PreAuthorize("#userId == principal.id")
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @PreAuthorize("#studentId == principal.id or hasRole('ADMIN')")
    public Student getStudentById(int studentId) {
        return studentRepository.getOne(studentId);
    }

    @Override
    public Instructor getInstructorById(int instructorId) {
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    @Override
    @PreAuthorize("#ungergradStudentId == principal.id or hasRole('ADMIN')")
    public UndergradStudent getUndergradStudentById(int ungergradStudentId) {
        return undergradStudentRepository.findById(ungergradStudentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

    }

    @Override
    @PreAuthorize("#gradStudentId == principal.id or hasRole('ADMIN')")
    public GradStudent getGradStudentById(int gradStudentId) {
        return gradStudentRepository.findById(gradStudentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<UndergradStudent> getAllUndergradStudents() {
        return undergradStudentRepository.findAll();
    }


    @Override
    public User getUserByUsername(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    @Override
    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<Instructor> getInstructorsWithUndergradCourses() {
        return instructorRepository.findAllWithUndergradCourses();
    }

    @Override
    public List<Instructor> getInstructorsWithGradCourses() {
        return instructorRepository.findAllWithGradCourses();
    }

    @Override
    public List<Instructor> getInstructorsByCourse(Course course) {
        return instructorRepository.findByCourses(course);
    }

    @Override
    @PreAuthorize("#passwordChangeForm.id == principal.id")
    public void updateUserPassword(ChangePasswordForm passwordChangeForm) {
        User user = userRepository.getOne(passwordChangeForm.getId());
        user.setPassword(passwordEncoder.encode(passwordChangeForm.getPassword()));
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("#updateData.id == principal.id or hasRole('ADMIN')")
    public void updateInstructorProfile(UserForm updateData) {
        Instructor instructor = instructorRepository.getOne(updateData.getId());
        instructor.update(updateData);
        instructorRepository.save(instructor);
    }

    @Override
    @PreAuthorize("#updateData.id == principal.id or hasRole('ADMIN')")
    public void updateUndergradStudentProfile(UserForm updateData) {
        UndergradStudent undergradStudent = undergradStudentRepository.getOne(updateData.getId());
        undergradStudent.update(updateData);
        undergradStudentRepository.save(undergradStudent);

    }

    @Override
    @PreAuthorize("#updateData.id == principal.id or hasRole('ADMIN')")
    public void updateGradStudentProfile(UserForm updateData) {
        GradStudent gradStudent = gradStudentRepository.getOne(updateData.getId());
        gradStudent.update(updateData);
        gradStudentRepository.save(gradStudent);

    }

    @Override
    public void saveNewUndergradStudent(UserForm userForm) {
        UndergradStudent undergradStudent = new UndergradStudent(userForm, passwordEncoder.encode(userForm.getPassword()), getRolesForUndergradStudents());
        undergradStudentRepository.save(undergradStudent);
    }

    @Override
    public void saveNewGradStudent(UserForm userForm) {
        GradStudent gradStudent = new GradStudent(userForm, passwordEncoder.encode(userForm.getPassword()), getRolesForInstructor());
        gradStudentRepository.save(gradStudent);
    }

    @Override
    public void saveNewInstructor(UserForm userForm) {
        WorkingPlan workingPlan = WorkingPlan.generateDefaultWorkingPlan();
        Instructor instructor = new Instructor(userForm, passwordEncoder.encode(userForm.getPassword()), getRolesForInstructor(), workingPlan);
        instructorRepository.save(instructor);
    }

    @Override
    public Collection<Role> getRolesForUndergradStudents() {
        HashSet<Role> roles = new HashSet();
        roles.add(roleRepository.findByName("ROLE_STUDENT_UNDERGRAD"));
        roles.add(roleRepository.findByName("ROLE_STUDENT"));
        return roles;
    }


    @Override
    public Collection<Role> getRoleForGradStudents() {
        HashSet<Role> roles = new HashSet();
        roles.add(roleRepository.findByName("ROLE_STUDENT_GRAD"));
        roles.add(roleRepository.findByName("ROLE_STUDENT"));
        return roles;
    }

    @Override
    public Collection<Role> getRolesForInstructor() {
        HashSet<Role> roles = new HashSet();
        roles.add(roleRepository.findByName("ROLE_INSTRUCTOR"));
        return roles;
    }


}

