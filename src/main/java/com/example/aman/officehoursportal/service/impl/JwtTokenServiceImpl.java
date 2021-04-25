package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenServiceImpl implements JwtTokenService {

    private final String jwtSecret;

    public JwtTokenServiceImpl(@Value(value = "${app.jwtSecret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    public String generateMeetingRejectionToken(Meeting meeting) {
        Date expiryDate = convertLocalDateTimeToDate(meeting.getEnd().plusDays(1));
        return Jwts.builder()
                .claim("meetingId", meeting.getId())
                .claim("studentId", meeting.getStudent().getId())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public String generateAcceptRejectionToken(Meeting meeting) {
        return Jwts.builder()
                .claim("meetingId", meeting.getId())
                .claim("instructorId", meeting.getInstructor().getId())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Error while token {} validation, error is {}", token, e.getMessage());
        }
        return false;

    }

    @Override
    public int getMeetingIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return (int) claims.get("meetingId");
    }

    @Override
    public int getStudentIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return (int) claims.get("studentId");
    }

    @Override
    public int getInstructorIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return (int) claims.get("instructorId");
    }

    @Override
    public Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(localDateTime);
        Instant instant = localDateTime.toInstant(zoneOffSet);
        return Date.from(instant);
    }
}
