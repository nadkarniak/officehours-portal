package com.example.aman.officehoursportal.entity.user.student;
import com.example.aman.officehoursportal.entity.Meeting;
import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.model.UserForm;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id_student")
public class Student extends User {

    @OneToMany(mappedBy = "student")
    private List<Meeting> meetings;

    public Student() {
    }

    public Student(UserForm userFormDTO, String encryptedPassword, Collection<Role> roles) {
        super(userFormDTO, encryptedPassword, roles);
    }


    public String getType() {
        if (super.hasRole("ROLE_STUDENT_GRAD")) {
            return "grad";
        } else if (super.hasRole("ROLE_STUDENT_UNDERGRAD")) {
            return "undergrad";
        }
        return "";
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}
