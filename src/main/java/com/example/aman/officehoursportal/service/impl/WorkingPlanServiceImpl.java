package com.example.aman.officehoursportal.service.impl;

import com.example.aman.officehoursportal.dao.WorkingPlanRepository;
import com.example.aman.officehoursportal.entity.WorkingPlan;
import com.example.aman.officehoursportal.model.TimePeroid;
import com.example.aman.officehoursportal.security.CustomUserDetails;
import com.example.aman.officehoursportal.service.WorkingPlanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WorkingPlanServiceImpl implements WorkingPlanService {

    private final WorkingPlanRepository workingPlanRepository;

    public WorkingPlanServiceImpl(WorkingPlanRepository workingPlanRepository) {
        this.workingPlanRepository = workingPlanRepository;
    }

    @Override
    @PreAuthorize("#updateData.instructor.id == principal.id")
    public void updateWorkingPlan(WorkingPlan updateData) {
        WorkingPlan workingPlan = workingPlanRepository.getOne(updateData.getId());
        workingPlan.getMonday().setWorkingHours(updateData.getMonday().getWorkingHours());
        workingPlan.getTuesday().setWorkingHours(updateData.getTuesday().getWorkingHours());
        workingPlan.getWednesday().setWorkingHours(updateData.getWednesday().getWorkingHours());
        workingPlan.getThursday().setWorkingHours(updateData.getThursday().getWorkingHours());
        workingPlan.getFriday().setWorkingHours(updateData.getFriday().getWorkingHours());
        workingPlan.getSaturday().setWorkingHours(updateData.getSaturday().getWorkingHours());
        workingPlan.getSunday().setWorkingHours(updateData.getSunday().getWorkingHours());
        workingPlanRepository.save(workingPlan);
    }

    @Override
    public void addBreakToWorkingPlan(TimePeroid breakToAdd, int planId, String dayOfWeek) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkingPlan workingPlan = workingPlanRepository.getOne(planId);
        if (workingPlan.getInstructor().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        workingPlan.getDay(dayOfWeek).getBreaks().add(breakToAdd);
        workingPlanRepository.save(workingPlan);
    }

    @Override
    public void deleteBreakFromWorkingPlan(TimePeroid breakToDelete, int planId, String dayOfWeek) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkingPlan workingPlan = workingPlanRepository.getOne(planId);
        if (workingPlan.getInstructor().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        workingPlan.getDay(dayOfWeek).getBreaks().remove(breakToDelete);
        workingPlanRepository.save(workingPlan);
    }


    @Override
    @PreAuthorize("#instructorId == principal.id")
    public WorkingPlan getWorkingPlanByInstructorId(int instructorId) {
        return workingPlanRepository.getWorkingPlanByInstructorId(instructorId);
    }


}
