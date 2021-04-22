package com.example.aman.officehoursportal.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class MeetingRegisterForm {

    private int courseId;
    private int instructorId;
    private int studentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime start;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime end;

    public MeetingRegisterForm() {
    }

    public MeetingRegisterForm(int courseId, int instructorId, LocalDateTime start, LocalDateTime end) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.start = start;
        this.end = end;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
