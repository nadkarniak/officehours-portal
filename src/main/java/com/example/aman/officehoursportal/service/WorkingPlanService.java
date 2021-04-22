package com.example.aman.officehoursportal.service;

import com.example.aman.officehoursportal.entity.WorkingPlan;
import com.example.aman.officehoursportal.model.TimePeroid;

public interface WorkingPlanService {
    void updateWorkingPlan(WorkingPlan workingPlan);

    void addBreakToWorkingPlan(TimePeroid breakToAdd, int planId, String dayOfWeek);

    void deleteBreakFromWorkingPlan(TimePeroid breakToDelete, int planId, String dayOfWeek);

    WorkingPlan getWorkingPlanByInstructorId(int instructorId);
}
