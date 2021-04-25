package com.example.aman.officehoursportal.validation;
import com.example.aman.officehoursportal.model.UserForm;
import com.example.aman.officehoursportal.validation.groups.UpdateGraduateStudent;
import com.example.aman.officehoursportal.validation.groups.UpdateUser;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UpdateGraduateStudentValidationTests {

    private ValidatorFactory factory;
    private Validator validator;

    @Before
    public void stup(){
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

@Test
public void shouldHave10ViolationsForEmptyFormWhenUpdateGraduateStudent(){
        UserForm form = new UserForm();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(form,UpdateUser.class,UpdateGraduateStudent.class);
        assertEquals(violations.size(),10);
    }
}
