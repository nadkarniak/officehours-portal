package com.example.aman.officehoursportal.dao;

import com.example.aman.officehoursportal.entity.WorkingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkingPlanRepository extends JpaRepository<WorkingPlan, Integer> {
    @Query("select w from WorkingPlan w where w.instructor.id = :instructorId")
    WorkingPlan getWorkingPlanByInstructorId(@Param("instructorId") int instructorId);
}
