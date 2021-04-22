package com.example.aman.officehoursportal.dao;

import com.example.aman.officehoursportal.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

}
