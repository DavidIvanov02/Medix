package com.medix.medix.services;

import com.medix.medix.dtos.doctor.CreateDoctorRequestDto;
import com.medix.medix.dtos.doctor.EditDoctorRequestDto;
import com.medix.medix.entities.Doctor;
import com.medix.medix.entities.Speciality;
import com.medix.medix.repositories.DoctorRepository;
import com.medix.medix.repositories.SpecialityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {
    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Doctor createSampleDoctor(Long id, String username, String password, String firstName, String lastName, boolean isGeneralPractitioner) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setUsername(username);
        doctor.setPassword(password);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setIsGeneralPractitioner(isGeneralPractitioner);
        return doctor;
    }

    private Speciality createSampleSpeciality(Long id, String name) {
        Speciality speciality = new Speciality();
        speciality.setId(id);
        speciality.setName(name);
        return speciality;
    }

    @Test
    void create_ValidDto_DoctorCreated() {
        CreateDoctorRequestDto dto = new CreateDoctorRequestDto();
        dto.setUsername("doctor1");
        dto.setPassword("password123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setIsGeneralPractitioner(true);
        dto.setSpecialities(Arrays.asList(1L, 2L));

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        Speciality speciality1 = createSampleSpeciality(1L, "Cardiology");
        Speciality speciality2 = createSampleSpeciality(2L, "Neurology");

        when(specialityRepository.findById(1L)).thenReturn(Optional.of(speciality1));
        when(specialityRepository.findById(2L)).thenReturn(Optional.of(speciality2));

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor doctor = invocation.getArgument(0);
            doctor.setId(1L);
            return doctor;
        });

        Doctor createdDoctor = doctorService.create(dto, bindingResult);

        assertNotNull(createdDoctor);
        assertEquals(1L, createdDoctor.getId());
        assertEquals("doctor1", createdDoctor.getUsername());
        assertEquals("encodedPassword123", createdDoctor.getPassword());
        assertEquals("John", createdDoctor.getFirstName());
        assertEquals("Doe", createdDoctor.getLastName());
        assertTrue(createdDoctor.getIsGeneralPractitioner());
        assertEquals(2, createdDoctor.getSpecialities().size());
        verify(specialityRepository, times(1)).findById(1L);
        verify(specialityRepository, times(1)).findById(2L);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void create_NullDto_BindingError() {
        Doctor createdDoctor = doctorService.create(null, bindingResult);

        assertNull(createdDoctor);
        verify(bindingResult, times(1)).rejectValue("username", "error.doctor", "Грешка при създаване на доктор");
        verifyNoMoreInteractions(doctorRepository, specialityRepository);
    }

    @Test
    void create_SpecialityNotFound_BindingError() {
        CreateDoctorRequestDto dto = new CreateDoctorRequestDto();
        dto.setUsername("doctor2");
        dto.setPassword("password456");
        dto.setFirstName("Bob");
        dto.setLastName("Builder");
        dto.setIsGeneralPractitioner(false);
        dto.setSpecialities(Arrays.asList(1L, 99L));

        when(passwordEncoder.encode("password456")).thenReturn("encodedPassword456");

        Speciality speciality1 = createSampleSpeciality(1L, "Pediatrics");
        when(specialityRepository.findById(1L)).thenReturn(Optional.of(speciality1));
        when(specialityRepository.findById(99L)).thenReturn(Optional.empty());

        Doctor createdDoctor = doctorService.create(dto, bindingResult);

        assertNull(createdDoctor);
        verify(specialityRepository, times(1)).findById(1L);
        verify(specialityRepository, times(1)).findById(99L);
        verify(bindingResult, times(1)).rejectValue("specialities", "error.doctor", "Грешка при добавяне на специалности");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void create_DataIntegrityViolation_BindingError() {
        CreateDoctorRequestDto dto = new CreateDoctorRequestDto();
        dto.setUsername("doctor3");
        dto.setPassword("password789");
        dto.setFirstName("Charlie");
        dto.setLastName("Chocolate");
        dto.setIsGeneralPractitioner(true);
        dto.setSpecialities(Arrays.asList(1L));

        when(passwordEncoder.encode("password789")).thenReturn("encodedPassword789");

        Speciality speciality1 = createSampleSpeciality(1L, "Dermatology");
        when(specialityRepository.findById(1L)).thenReturn(Optional.of(speciality1));

        when(doctorRepository.save(any(Doctor.class))).thenThrow(DataIntegrityViolationException.class);

        Doctor createdDoctor = doctorService.create(dto, bindingResult);

        assertNull(createdDoctor);
        verify(specialityRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verify(bindingResult, times(1)).rejectValue("specialities", "error.doctor", "Грешка при създаване на доктор");
    }

    @Test
    void update_ValidDto_DoctorUpdated() {
        Long doctorId = 1L;
        EditDoctorRequestDto dto = new EditDoctorRequestDto();
        dto.setUsername("updatedDoctor");
        dto.setPassword("newPassword123");
        dto.setFirstName("JohnUpdated");
        dto.setLastName("DoeUpdated");
        dto.setIsGeneralPractitioner(false);
        dto.setSpecialities(Arrays.asList(2L, 3L));

        Doctor existingDoctor = createSampleDoctor(doctorId, "doctor1", "oldPassword", "John", "Doe", true);
        Speciality speciality2 = createSampleSpeciality(2L, "Oncology");
        Speciality speciality3 = createSampleSpeciality(3L, "Radiology");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(specialityRepository.findById(2L)).thenReturn(Optional.of(speciality2));
        when(specialityRepository.findById(3L)).thenReturn(Optional.of(speciality3));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword123");
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Doctor currentUser = createSampleDoctor(doctorId, "doctor1", "oldPassword", "John", "Doe", true);
        when(authentication.getPrincipal()).thenReturn(currentUser);

        Doctor updatedDoctor = doctorService.update(doctorId, dto, bindingResult);

        assertNotNull(updatedDoctor);
        assertEquals("updatedDoctor", updatedDoctor.getUsername());
        assertEquals("encodedNewPassword123", updatedDoctor.getPassword());
        assertEquals("JohnUpdated", updatedDoctor.getFirstName());
        assertEquals("DoeUpdated", updatedDoctor.getLastName());
        assertFalse(updatedDoctor.getIsGeneralPractitioner());
        assertEquals(2, updatedDoctor.getSpecialities().size());
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(specialityRepository, times(1)).findById(2L);
        verify(specialityRepository, times(1)).findById(3L);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void update_NullDto_BindingError() {
        Long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenThrow(new EntityNotFoundException("Doctor not found"));

        Doctor updatedDoctor = doctorService.update(doctorId, null, bindingResult);

        assertNull(updatedDoctor);
        verify(bindingResult, times(1)).rejectValue("username", "error.doctor", "Докторът не е намерен");

        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository, specialityRepository);
    }

    @Test
    void update_DoctorNotFound_BindingError() {
        Long doctorId = 99L;
        EditDoctorRequestDto dto = new EditDoctorRequestDto();
        dto.setUsername("nonExistentDoctor");
        dto.setPassword("password000");
        dto.setFirstName("Ghost");
        dto.setLastName("Doctor");
        dto.setIsGeneralPractitioner(false);
        dto.setSpecialities(Arrays.asList(1L));

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        Doctor updatedDoctor = doctorService.update(doctorId, dto, bindingResult);

        assertNull(updatedDoctor);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(bindingResult, times(1)).rejectValue("username", "error.doctor", "Докторът не е намерен");
        verifyNoMoreInteractions(specialityRepository, doctorRepository);
    }

    @Test
    void update_SpecialityNotFound_BindingError() {
        Long doctorId = 1L;
        EditDoctorRequestDto dto = new EditDoctorRequestDto();
        dto.setUsername("updatedDoctor");
        dto.setPassword("newPassword123");
        dto.setFirstName("JohnUpdated");
        dto.setLastName("DoeUpdated");
        dto.setIsGeneralPractitioner(false);
        dto.setSpecialities(Arrays.asList(2L, 99L));

        Doctor existingDoctor = createSampleDoctor(doctorId, "doctor1", "oldPassword", "John", "Doe", true);
        Speciality speciality2 = createSampleSpeciality(2L, "Oncology");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(specialityRepository.findById(2L)).thenReturn(Optional.of(speciality2));
        when(specialityRepository.findById(99L)).thenReturn(Optional.empty());

        Doctor updatedDoctor = doctorService.update(doctorId, dto, bindingResult);

        assertNull(updatedDoctor);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(specialityRepository, times(1)).findById(2L);
        verify(specialityRepository, times(1)).findById(99L);
        verify(bindingResult, times(1)).rejectValue("specialities", "error.doctor", "Грешка при добавяне на специалности");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void update_DataIntegrityViolation_BindingError() {
        Long doctorId = 1L;
        EditDoctorRequestDto dto = new EditDoctorRequestDto();
        dto.setUsername("doctorConflict");
        dto.setPassword("newPasswordConflict");
        dto.setFirstName("JohnConflict");
        dto.setLastName("DoeConflict");
        dto.setIsGeneralPractitioner(true);
        dto.setSpecialities(Arrays.asList(1L));

        Doctor existingDoctor = createSampleDoctor(doctorId, "doctor1", "oldPassword", "John", "Doe", true);
        Speciality speciality1 = createSampleSpeciality(1L, "Dermatology");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(specialityRepository.findById(1L)).thenReturn(Optional.of(speciality1));
        when(passwordEncoder.encode("newPasswordConflict")).thenReturn("encodedNewPasswordConflict");
        when(doctorRepository.save(any(Doctor.class))).thenThrow(DataIntegrityViolationException.class);

        Doctor currentUser = createSampleDoctor(doctorId, "doctor1", "oldPassword", "John", "Doe", true);
        when(authentication.getPrincipal()).thenReturn(currentUser);

        Doctor updatedDoctor = doctorService.update(doctorId, dto, bindingResult);

        assertNull(updatedDoctor);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(specialityRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPasswordConflict");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verify(bindingResult, times(1)).rejectValue("isGeneralPractitioner", "error.doctor", "Грешка при редактиране на доктор");
    }

    @Test
    void findById_DoctorExists_ReturnsDoctor() {
        Long doctorId = 1L;
        Doctor doctor = createSampleDoctor(doctorId, "doctor1", "pass1", "John", "Doe", true);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Doctor foundDoctor = doctorService.findById(doctorId);

        assertNotNull(foundDoctor);
        assertEquals(doctorId, foundDoctor.getId());
        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    void findById_DoctorDoesNotExist_ThrowsException() {
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> doctorService.findById(doctorId));
        verify(doctorRepository, times(1)).findById(doctorId);
    }
}
