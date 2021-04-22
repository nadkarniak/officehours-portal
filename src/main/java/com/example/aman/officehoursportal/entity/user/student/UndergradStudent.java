package com.example.aman.officehoursportal.entity.user.student;

import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.model.UserForm;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Collection;

@Entity
@Table(name = "undergrad_students")
@PrimaryKeyJoinColumn(name = "id_student")
public class UndergradStudent extends Student {

    public UndergradStudent() {
    }

    public UndergradStudent(UserForm userFormDTO, String encryptedPassword, Collection<Role> roles) {
        super(userFormDTO, encryptedPassword, roles);
    }


}
