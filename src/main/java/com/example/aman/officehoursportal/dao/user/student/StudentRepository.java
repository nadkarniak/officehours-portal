package com.example.aman.officehoursportal.dao.user.student;

import com.example.aman.officehoursportal.dao.user.CommonUserRepository;
import com.example.aman.officehoursportal.entity.user.student.Student;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface StudentRepository extends CommonUserRepository<Student> {
    @Query("SELECT S FROM Student S INNER JOIN Role R on S.id=R.id WHERE CONCAT(S.firstName,' ', S.lastName,' ',S.email,' ',S.mobile,' ',S.userName, ' ',R.name) LIKE %:keyword%")
    public List<Student> search(String keyword);
}

