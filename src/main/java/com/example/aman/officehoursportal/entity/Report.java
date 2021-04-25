package com.example.aman.officehoursportal.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    @Column(name = "number")
    private String number;

    @Column(name = "status")
    private String status;

    @Column(name = "total_credits")
    private int totalCredits;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(name = "issued")
    private LocalDateTime issued;

    @OneToMany(mappedBy = "report")
    private List<Meeting> meetings;

    public Report() {
    }

    public Report(String number, String status, LocalDateTime issued, List<Meeting> meetings2) {
        this.number = number;
        this.status = status;
        this.issued = issued;
        this.meetings = new ArrayList<>();
        for (Meeting a : meetings2) {
            this.meetings.add(a);
            a.setReport(this);
            totalCredits += a.getCourse().getCredits();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getIssued() {
        return issued;
    }

    public void setIssued(LocalDateTime issued) {
        this.issued = issued;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}
