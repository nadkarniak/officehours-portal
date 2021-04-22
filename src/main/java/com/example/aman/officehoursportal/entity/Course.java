package com.example.aman.officehoursportal.entity;

import com.example.aman.officehoursportal.entity.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "credits")
    private int credits;

    @Column(name = "duration")
    private int duration;

    @Column(name = "editable")
    private boolean editable;

    @Column(name = "target")
    private String targetStudent;

    @ManyToMany
    @JoinTable(name = "courses_instructors", joinColumns = @JoinColumn(name = "id_course"), inverseJoinColumns = @JoinColumn(name = "id_user"))
    private List<User> instructors;

    public Course() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<User> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<User> instructors) {
        this.instructors = instructors;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getTargetStudent() {
        return targetStudent;
    }

    public void setTargetStudent(String targetStudent) {
        this.targetStudent = targetStudent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return super.getId().equals(course.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
