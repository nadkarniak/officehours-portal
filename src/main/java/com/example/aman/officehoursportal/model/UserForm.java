package com.example.aman.officehoursportal.model;

import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.entity.user.student.GradStudent;
import com.example.aman.officehoursportal.entity.user.student.UndergradStudent;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import com.example.aman.officehoursportal.validation.FieldsMatches;
import com.example.aman.officehoursportal.validation.UniqueUsername;
import com.example.aman.officehoursportal.validation.groups.*;

import javax.validation.constraints.*;
import java.util.List;

@FieldsMatches(field = "password", matchingField = "matchingPassword", groups = {CreateUser.class})
public class UserForm {

    @NotNull(groups = {UpdateUser.class})
    @Min(value = 1, groups = {UpdateUser.class})
    private int id;

    @UniqueUsername(groups = {CreateUser.class})
    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "Username should have 5-15 letters")
    @NotBlank(groups = {CreateUser.class})
    private String userName;

    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "Password should have 5-15 letters")
    @NotBlank(groups = {CreateUser.class})
    private String password;

    @Size(min = 5, max = 15, groups = {CreateUser.class}, message = "Password should have 5-15 letters")
    @NotBlank(groups = {CreateUser.class})
    private String matchingPassword;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "First name cannot be empty")
    private String firstName;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Last name cannot be empty")
    private String lastName;

    @Email(groups = {CreateUser.class, UpdateUser.class}, message = "Email not valid!")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Email cannot be empty")
    private String email;

    @Pattern(groups = {CreateUser.class, UpdateUser.class}, regexp = "[0-9]{10}", message = "Please enter valid mobile phone")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Mobile phone cannot be empty")
    private String mobile;

    @Size(groups = {CreateUser.class, UpdateUser.class}, min = 5, max = 30, message = "Wrong street!")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Street cannot be empty")
    private String street;

    @Pattern(groups = {CreateUser.class, UpdateUser.class}, regexp = "[0-9]{5}", message = "Please enter valid postcode")
    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "Post code cannot be empty")
    private String postcode;

    @NotBlank(groups = {CreateUser.class, UpdateUser.class}, message = "City cannot be empty")
    private String city;

    /*
     * Grad Students only:
     * */
    @NotBlank(groups = {CreateGraduateStudent.class, UpdateGraduateStudent.class}, message = "University cannot be empty")
    private String univName;

    @Pattern(groups = {CreateGraduateStudent.class, UpdateGraduateStudent.class}, regexp = "[0-9]{10}", message = "Please enter valid ID number")
    @NotBlank(groups = {CreateGraduateStudent.class, UpdateGraduateStudent.class}, message = "College ID number cannot be empty")
    private String cidNumber;

    /*
     * Instructors only:
     * */
    @NotNull(groups = {CreateInstructor.class, UpdateInstructor.class})
    private List<Course> courses;


    public UserForm() {
    }

    public UserForm(User user) {
        this.setId(user.getId());
        this.setUserName(user.getUserName());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setCity(user.getCity());
        this.setStreet(user.getStreet());
        this.setPostcode(user.getPostcode());
        this.setMobile(user.getMobile());
    }

    public UserForm(Instructor instructor) {
        this((User) instructor);
        this.setCourses(instructor.getCourses());
    }

    public UserForm(UndergradStudent undergradStudent) {
        this((User) undergradStudent);
    }

    public UserForm(GradStudent gradStudent) {
        this((User) gradStudent);
        this.setUnivName(gradStudent.getUnivName());
        this.setCidNumber(gradStudent.getCidNumber());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getUnivName() {
        return univName;
    }

    public void setUnivName(String univName) {
        this.univName = univName;
    }

    public String getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(String cidNumber) {
        this.cidNumber = cidNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

}