package com.example.aman.officehoursportal.validation;

import com.example.aman.officehoursportal.entity.user.User;
import com.example.aman.officehoursportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(final UniqueUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        String userName = (String) obj;
        User user = userService.getUserByUsername(userName);
        return user == null;
    }

}