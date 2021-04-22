package com.example.aman.officehoursportal.entity;

import com.example.aman.officehoursportal.entity.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class ChatMessage extends BaseEntity implements Comparable<ChatMessage> {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "id_author")
    private User author;

    @ManyToOne
    @JoinColumn(name = "id_meeting")
    private Meeting meeting;

    public ChatMessage() {

    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    @Override
    public int compareTo(ChatMessage o) {
        return this.createdAt.compareTo(o.getCreatedAt());
    }
}
