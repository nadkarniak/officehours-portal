package com.example.aman.officehoursportal.dao;

import com.example.aman.officehoursportal.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String roleName);
}
