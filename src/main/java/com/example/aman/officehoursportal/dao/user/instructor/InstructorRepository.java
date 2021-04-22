package com.example.aman.officehoursportal.dao.user.instructor;

import com.example.aman.officehoursportal.dao.user.CommonUserRepository;
import com.example.aman.officehoursportal.entity.Course;
import com.example.aman.officehoursportal.entity.user.instructor.Instructor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstructorRepository extends CommonUserRepository<Instructor> {

    List<Instructor> findByCourses(Course course);

    @Query("select distinct p from Instructor p inner join p.courses w where w.targetStudent = 'undergrad'")
    List<Instructor> findAllWithUndergradCourses();

    @Query("select distinct p from Instructor p inner join p.courses w where w.targetStudent = 'grad'")
    List<Instructor> findAllWithGradCourses();
}
