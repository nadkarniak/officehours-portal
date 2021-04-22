package com.example.aman.officehoursportal.dao;

import com.example.aman.officehoursportal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("select w from Course w inner join w.instructors p where p.id in :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") int instructorId);

    @Query("select w from Course w where w.targetStudent = :target ")
    List<Course> findByTargetStudent(@Param("target") String targetStudent);

    @Query("select w from Course w inner join w.instructors p where p.id in :instructorId and w.targetStudent = :target ")
    List<Course> findByTargetStudentAndInstructorId(@Param("target") String targetStudent, @Param("instructorId") int instructorId);
}
