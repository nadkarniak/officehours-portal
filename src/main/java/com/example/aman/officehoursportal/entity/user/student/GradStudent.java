package com.example.aman.officehoursportal.entity.user.student;

import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.model.UserForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Collection;

@Entity
@Table(name = "grad_students")
@PrimaryKeyJoinColumn(name = "id_student")
public class GradStudent extends Student {

    @Column(name = "univ_name")
    private String univName;

    @Column(name = "cid_number")
    private String cidNumber;


    public GradStudent() {
    }

    public GradStudent(UserForm userFormDTO, String encryptedPassword, Collection<Role> roles) {
        super(userFormDTO, encryptedPassword, roles);
        this.univName = userFormDTO.getUnivName();
        this.cidNumber = userFormDTO.getCidNumber();
    }

    @Override
    public void update(UserForm updateData) {
        super.update(updateData);
        this.univName = updateData.getUnivName();
        this.cidNumber = updateData.getCidNumber();
    }

    public String getUnivName() {
        return univName;
    }

    public void setUnivName(String univName) {
        this.univName = univName;
    }

    public String getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(String cidNumber) {
        this.cidNumber = cidNumber;
    }

}
