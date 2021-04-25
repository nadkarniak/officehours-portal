package com.example.aman.officehoursportal.service.user;

import com.example.aman.officehoursportal.dao.RoleRepository;
import com.example.aman.officehoursportal.dao.user.student.UndergradStudentRepository;
import com.example.aman.officehoursportal.entity.user.Role;
import com.example.aman.officehoursportal.entity.user.student.UndergradStudent;
import com.example.aman.officehoursportal.model.UserForm;
import com.example.aman.officehoursportal.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTestsForUndergradStudent {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UndergradStudentRepository undergradStudentRepository;

    @InjectMocks
    private UserServiceImpl userService;

    UserForm undergradUserForm;
    UndergradStudent undergradStudent;
    Optional<UndergradStudent> optionalUndergradStudent;

    private int userId;
    private String userName;
    private String password;
    private String matchingPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String street;
    private String postcode;
    private String city;

    String passwordEncoded;

    private String roleNameStudent;
    private String roleNameUndergradStudent;
    private Role roleStudent;
    private Role roleUndergradStudent;
    Collection<Role> undergradStudentRoles;


    @Before
    public void initObjects(){

        userId = 1;
        userName = "username";
        password = "password";
        matchingPassword = "password";
        firstName = "firstname";
        lastName = "lastname";
        email = "email@example.com";
        mobile = "1234567891";
        street = "street";
        postcode = "12345";
        city = "city";

        passwordEncoded = "xxxx";

        undergradUserForm = new UserForm();
        undergradUserForm.setUserName(userName);
        undergradUserForm.setPassword(password);
        undergradUserForm.setMatchingPassword(matchingPassword);
        undergradUserForm.setFirstName(firstName);
        undergradUserForm.setLastName(lastName);
        undergradUserForm.setEmail(email);
        undergradUserForm.setMobile(mobile);
        undergradUserForm.setStreet(street);
        undergradUserForm.setPostcode(postcode);
        undergradUserForm.setCity(city);
        undergradUserForm.setId(userId);

        roleNameStudent = "ROLE_STUDENT";
        roleNameUndergradStudent = "ROLE_STUDENT_UNDERGRAD";
        roleStudent = new Role(roleNameStudent);
        roleUndergradStudent = new Role(roleNameUndergradStudent);
        undergradStudentRoles =  new HashSet<>();
        undergradStudentRoles.add(roleStudent);
        undergradStudentRoles.add(roleUndergradStudent);


    }

    @Test
    public void shouldSaveNewUndergradStudent(){
        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.saveNewUndergradStudent(undergradUserForm);

        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
    }

    @Test
    public void shouldEncodePasswordWhenForNewUndergradStudent(){
        when(passwordEncoder.encode(password)).thenReturn(passwordEncoded);

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.saveNewUndergradStudent(undergradUserForm);

        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getPassword(),passwordEncoded);
    }

    @Test
    public void userFormDataShouldMatchUndergradStudentObject(){

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.saveNewUndergradStudent(undergradUserForm);

        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getUserName(),undergradUserForm.getUserName());
        assertEquals(argumentCaptor.getValue().getFirstName(),undergradUserForm.getFirstName());
        assertEquals(argumentCaptor.getValue().getLastName(),undergradUserForm.getLastName());
        assertEquals(argumentCaptor.getValue().getEmail(),undergradUserForm.getEmail());
        assertEquals(argumentCaptor.getValue().getMobile(),undergradUserForm.getMobile());
        assertEquals(argumentCaptor.getValue().getStreet(),undergradUserForm.getStreet());
        assertEquals(argumentCaptor.getValue().getCity(),undergradUserForm.getCity());
        assertEquals(argumentCaptor.getValue().getPostcode(),undergradUserForm.getPostcode());
    }

    @Test
    public void shouldAssignTwoRolesForUndergradStudent(){
        doReturn(roleUndergradStudent).when(roleRepository).findByName(roleNameUndergradStudent);
        doReturn(roleStudent).when(roleRepository).findByName(roleNameStudent);

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.saveNewUndergradStudent(undergradUserForm);

        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getRoles().size(),2);
    }

    @Test
    public void shouldAssignCorrectRolesForUndergradStudent(){
        doReturn(roleUndergradStudent).when(roleRepository).findByName(roleNameUndergradStudent);
        doReturn(roleStudent).when(roleRepository).findByName(roleNameStudent);

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.saveNewUndergradStudent(undergradUserForm);

        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().hasRole(roleNameUndergradStudent),true);
        assertEquals(argumentCaptor.getValue().hasRole(roleNameStudent),true);
    }

    @Test
    public void shouldUpdateUndergradStudentProfileData(){
        UndergradStudent customerToBeUpdated = new UndergradStudent();
        customerToBeUpdated.setId(userId);

        doReturn(customerToBeUpdated).when(undergradStudentRepository).getOne(userId);

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.updateUndergradStudentProfile(undergradUserForm);
        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getFirstName(),undergradUserForm.getFirstName());
        assertEquals(argumentCaptor.getValue().getLastName(),undergradUserForm.getLastName());
        assertEquals(argumentCaptor.getValue().getEmail(),undergradUserForm.getEmail());
        assertEquals(argumentCaptor.getValue().getMobile(),undergradUserForm.getMobile());
        assertEquals(argumentCaptor.getValue().getStreet(),undergradUserForm.getStreet());
        assertEquals(argumentCaptor.getValue().getCity(),undergradUserForm.getCity());
        assertEquals(argumentCaptor.getValue().getPostcode(),undergradUserForm.getPostcode());
    }

    @Test
    public void shouldNotAffectUsernameAndPasswordAndRolesWhileUndergradStudentProfileUpdate(){
        UndergradStudent customerToBeUpdated = new UndergradStudent();
        String currentUsername = "username2";
        String currentPassword = "password2";
        customerToBeUpdated.setUserName(currentUsername);
        customerToBeUpdated.setPassword(currentPassword);
        customerToBeUpdated.setRoles(undergradStudentRoles);
        doReturn(customerToBeUpdated).when(undergradStudentRepository).getOne(userId);

        ArgumentCaptor<UndergradStudent> argumentCaptor = ArgumentCaptor.forClass(UndergradStudent.class);
        userService.updateUndergradStudentProfile(undergradUserForm);
        verify(undergradStudentRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getUserName(),currentUsername);
        assertEquals(argumentCaptor.getValue().getPassword(),currentPassword);
        assertEquals(argumentCaptor.getValue().getRoles(),undergradStudentRoles);
    }

    @Test
    public void shouldFindUndergradStudentById() {
        undergradStudent = new UndergradStudent();
        undergradStudent.setId(userId);
        undergradStudent.setUserName(userName);
        optionalUndergradStudent = Optional.of(undergradStudent);
        when(undergradStudentRepository.findById(userId)).thenReturn(optionalUndergradStudent);
        assertEquals(optionalUndergradStudent.get(), userService.getUndergradStudentById(userId));
        verify(undergradStudentRepository,times(1)).findById(userId);
    }

}


