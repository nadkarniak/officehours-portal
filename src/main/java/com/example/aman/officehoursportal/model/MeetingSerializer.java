package com.example.aman.officehoursportal.model;

import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.MeetingStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZoneOffset;

public class MeetingSerializer extends StdSerializer<Meeting> {

    public MeetingSerializer() {
        this(null);
    }

    public MeetingSerializer(Class<Meeting> t) {
        super(t);
    }

    @Override
    public void serialize(Meeting meeting, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", meeting.getId());
        gen.writeStringField("title", meeting.getCourse().getName());
        gen.writeNumberField("start", meeting.getStart().toInstant(ZoneOffset.UTC).toEpochMilli());
        gen.writeNumberField("end", meeting.getEnd().toInstant(ZoneOffset.UTC).toEpochMilli());
        gen.writeStringField("url", "/meetings/" + meeting.getId());
        gen.writeStringField("color", meeting.getStatus().equals(MeetingStatus.SCHEDULED) ? "#28a745" : "grey");
        gen.writeEndObject();
    }
}
