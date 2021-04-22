package com.example.aman.officehoursportal.entity;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import com.example.aman.officehoursportal.entity.user.student.Student;
import com.example.aman.officehoursportal.model.MeetingSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "meetings")
@JsonSerialize(using = MeetingSerializer.class)
public class Meeting extends BaseEntity implements Comparable<Meeting> {

    @Column(name = "start")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime start;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "end")
    private LocalDateTime end;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @OneToOne
    @JoinColumn(name = "id_canceler")
    private User canceler;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeetingStatus status;

    @ManyToOne
    @JoinColumn(name = "id_student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_instructor")
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "id_course")
    private Course course;

    @OneToMany(mappedBy = "meeting")
    private List<ChatMessage> chatMessages;

    @ManyToOne
    @JoinColumn(name = "id_report")
    private Report report;

    @OneToOne(mappedBy = "requested", cascade = {CascadeType.ALL})
    private ExchangeRequest exchangeRequest;

    public Meeting() {
    }

    public Meeting(LocalDateTime start, LocalDateTime end, Student student, Instructor instructor, Course course) {
        this.start = start;
        this.end = end;
        this.student = student;
        this.instructor = instructor;
        this.course = course;
    }

    @Override
    public int compareTo(Meeting o) {
        return this.getStart().compareTo(o.getStart());
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public List<ChatMessage> getChatMessages() {
        Collections.sort(chatMessages);
        return chatMessages;
    }

    public User getCanceler() {
        return canceler;
    }

    public void setCanceler(User canceler) {
        this.canceler = canceler;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ExchangeRequest getExchangeRequest() {
        return exchangeRequest;
    }

    public void setExchangeRequest(ExchangeRequest exchangeRequest) {
        this.exchangeRequest = exchangeRequest;
    }
}
