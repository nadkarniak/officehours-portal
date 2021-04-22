package com.example.aman.officehoursportal.entity.user.instructor;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.WorkingPlan;
import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.model.UserForm;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "instructors")
@PrimaryKeyJoinColumn(name = "id_instructor")
public class Instructor extends User {

    @OneToMany(mappedBy = "instructor")
    private List<Meeting> meetings;

    @ManyToMany
    @JoinTable(name = "courses_instructors", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_course"))
    private List<Course> courses;

    @OneToOne(mappedBy = "instructor", cascade = {CascadeType.ALL})
    private WorkingPlan workingPlan;

    public Instructor() {
    }

    public Instructor(UserForm userFormDTO, String encryptedPassword, Collection<Role> roles, WorkingPlan workingPlan) {
        super(userFormDTO, encryptedPassword, roles);
        this.workingPlan = workingPlan;
        workingPlan.setInstructor(this);
        this.courses = userFormDTO.getCourses();
    }

    @Override
    public void update(UserForm updateData) {
        super.update(updateData);
        this.courses = updateData.getCourses();
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public WorkingPlan getWorkingPlan() {
        return workingPlan;
    }

    public void setWorkingPlan(WorkingPlan workingPlan) {
        this.workingPlan = workingPlan;
    }

    public List<Course> getGradCourses() {
        List<Course> graduateCourses = new ArrayList<>();
        for (Course w : courses) {
            if (w.getTargetStudent().equals("grad")) {
                graduateCourses.add(w);
            }
        }
        return graduateCourses;
    }

    public List<Course> getUndergradCourses() {
        List<Course> undergradCourses = new ArrayList<>();
        for (Course w : courses) {
            if (w.getTargetStudent().equals("undergrad")) {
                undergradCourses.add(w);
            }
        }
        return undergradCourses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Instructor instructor = (Instructor) o;
        return instructor.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetings, courses, workingPlan);
    }

}
