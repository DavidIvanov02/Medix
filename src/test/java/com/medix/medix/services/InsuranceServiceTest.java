package com.medix.medix.services;

import com.medix.medix.dtos.insurance.EditInsuranceRequestDto;
import com.medix.medix.dtos.insurance.CreateInsuranceRequestDto;
import com.medix.medix.entities.Insurance;
import com.medix.medix.entities.Patient;
import com.medix.medix.repositories.InsuranceRepository;
import com.medix.medix.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsuranceServiceTest {
    @InjectMocks
    private InsuranceService insuranceService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private InsuranceRepository insuranceRepository;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Insurance createSampleInsurance(Long id, Patient patient, LocalDate insuranceDate, LocalDate dateOfPayment, Double sum) {
        Insurance insurance = new Insurance();
        insurance.setId(id);
        insurance.setPatient(patient);
        insurance.setInsuranceYear(insuranceDate.getYear());
        insurance.setInsuranceMonth(insuranceDate.getMonthValue());
        insurance.setDateOfPayment(dateOfPayment);
        insurance.setSum(sum);

        return insurance;
    }

    private Patient createSamplePatient(Long id, String name) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setUsername(name);

        return patient;
    }

    @Test
    void create_ValidDto_InsuranceCreated() {
        CreateInsuranceRequestDto dto = new CreateInsuranceRequestDto();

        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(5);
        dto.setDateOfPayment(LocalDate.of(2023, 5, 15));
        dto.setSum(100.0);

        Patient patient = createSamplePatient(1L, "John Doe");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1))).thenReturn(false);
        when(insuranceRepository.save(any(Insurance.class))).thenAnswer(invocation -> {
            Insurance insurance = invocation.getArgument(0);
            insurance.setId(1L);
            return insurance;
        });

        Insurance createdInsurance = insuranceService.create(dto, bindingResult);

        assertNotNull(createdInsurance);
        assertEquals(1L, createdInsurance.getId());
        assertEquals(patient, createdInsurance.getPatient());
        assertEquals(LocalDate.of(2023, 5, 1), createdInsurance.getInsuranceDate());
        assertEquals(LocalDate.of(2023, 5, 15), createdInsurance.getDateOfPayment());
        assertEquals(100.0, createdInsurance.getSum());
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1));
        verify(insuranceRepository, times(1)).save(any(Insurance.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void create_NullDto_BindingError() {
        Insurance createdInsurance = insuranceService.create(null, bindingResult);

        assertNull(createdInsurance);
        verify(bindingResult, times(1)).rejectValue("date", "error.insurance", "Грешка при създаване на осигуровка");
        verifyNoMoreInteractions(patientRepository, insuranceRepository);
    }

    @Test
    void create_PatientNotFound_BindingError() {
        CreateInsuranceRequestDto dto = new CreateInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(5);
        dto.setDateOfPayment(LocalDate.of(2023, 5, 15));
        dto.setSum(100.0);

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Insurance createdInsurance = insuranceService.create(dto, bindingResult);

        assertNull(createdInsurance);
        verify(patientRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("patientId", "error.insurance", "Пациентът не съществува");
        verifyNoMoreInteractions(insuranceRepository);
    }

    @Test
    void create_ExistingInsurance_BindingError() {
        CreateInsuranceRequestDto dto = new CreateInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(5);
        dto.setDateOfPayment(LocalDate.of(2023, 5, 15));
        dto.setSum(100.0);

        Patient patient = createSamplePatient(1L, "John Doe");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1))).thenReturn(true);

        Insurance createdInsurance = insuranceService.create(dto, bindingResult);

        assertNull(createdInsurance);
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1));
        verify(bindingResult, times(1)).rejectValue("insuranceMonth", "error.insurance", "Пациентът вече има платена осигуровка за този месец");
        verifyNoMoreInteractions(insuranceRepository);
    }

    @Test
    void create_Exception_BindingError() {
        CreateInsuranceRequestDto dto = new CreateInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(5);
        dto.setDateOfPayment(LocalDate.of(2023, 5, 15));
        dto.setSum(100.0);

        Patient patient = createSamplePatient(1L, "John Doe");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1))).thenReturn(false);
        when(insuranceRepository.save(any(Insurance.class))).thenThrow(new RuntimeException("DB Error"));

        Insurance createdInsurance = insuranceService.create(dto, bindingResult);

        assertNull(createdInsurance);
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDate(1L, LocalDate.of(2023, 5, 1));
        verify(insuranceRepository, times(1)).save(any(Insurance.class));
        verify(bindingResult, times(1)).rejectValue("date", "error.insurance", "Грешка при създаване на осигуровка");
    }

    @Test
    void update_ValidDto_InsuranceUpdated() {
        EditInsuranceRequestDto dto = new EditInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(6);
        dto.setDateOfPayment(LocalDate.of(2023, 6, 10));
        dto.setSum(150.0);

        Patient patient = createSamplePatient(1L, "John Doe");
        Insurance existingInsurance = createSampleInsurance(1L, patient, LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 15), 100.0);

        when(insuranceRepository.findById(1L)).thenReturn(Optional.of(existingInsurance));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 6, 1), 1L)).thenReturn(false);
        when(insuranceRepository.save(any(Insurance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Insurance updatedInsurance = insuranceService.update(1L, dto, bindingResult);

        assertNotNull(updatedInsurance);
        assertEquals(1L, updatedInsurance.getId());
        assertEquals(patient, updatedInsurance.getPatient());
        assertEquals(LocalDate.of(2023, 6, 1), updatedInsurance.getInsuranceDate());
        assertEquals(LocalDate.of(2023, 6, 10), updatedInsurance.getDateOfPayment());
        assertEquals(150.0, updatedInsurance.getSum());
        verify(insuranceRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 6, 1), 1L);
        verify(insuranceRepository, times(1)).save(any(Insurance.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void update_NullDto_BindingError() {
        Insurance updatedInsurance = insuranceService.update(1L, null, bindingResult);

        assertNull(updatedInsurance);
        verify(bindingResult, times(1)).rejectValue("date", "error.insurance", "Грешка при редактиране на осигуровка");
        verifyNoMoreInteractions(insuranceRepository, patientRepository);
    }

    @Test
    void update_InsuranceNotFound_BindingError() {
        EditInsuranceRequestDto dto = new EditInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(6);
        dto.setDateOfPayment(LocalDate.of(2023, 6, 10));
        dto.setSum(150.0);

        when(insuranceRepository.findById(1L)).thenReturn(Optional.empty());

        Insurance updatedInsurance = insuranceService.update(1L, dto, bindingResult);

        assertNull(updatedInsurance);
        verify(insuranceRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("date", "error.insurance", "Специалността не съществува");
        verifyNoMoreInteractions(patientRepository, insuranceRepository);
    }

    @Test
    void update_PatientNotFound_BindingError() {
        EditInsuranceRequestDto dto = new EditInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(6);
        dto.setDateOfPayment(LocalDate.of(2023, 6, 10));
        dto.setSum(150.0);

        Insurance existingInsurance = createSampleInsurance(1L, null, LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 15), 100.0);

        when(insuranceRepository.findById(1L)).thenReturn(Optional.of(existingInsurance));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Insurance updatedInsurance = insuranceService.update(1L, dto, bindingResult);

        assertNull(updatedInsurance);
        verify(insuranceRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("patientId", "error.insurance", "Пациентът не съществува");
        verifyNoMoreInteractions(insuranceRepository);
    }

    @Test
    void update_ExistingInsurance_BindingError() {
        EditInsuranceRequestDto dto = new EditInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(5);
        dto.setDateOfPayment(LocalDate.of(2023, 5, 20));
        dto.setSum(120.0);

        Patient patient = createSamplePatient(1L, "John Doe");
        Insurance existingInsurance = createSampleInsurance(1L, patient, LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 15), 100.0);

        when(insuranceRepository.findById(1L)).thenReturn(Optional.of(existingInsurance));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 5, 1), 1L)).thenReturn(true);

        Insurance updatedInsurance = insuranceService.update(1L, dto, bindingResult);

        assertNull(updatedInsurance);
        verify(insuranceRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 5, 1), 1L);
        verify(bindingResult, times(1)).rejectValue("insuranceMonth", "error.insurance", "Пациентът вече има платена осигуровка за този месец");
        verifyNoMoreInteractions(insuranceRepository);
    }

    @Test
    void update_Exception_BindingError() {
        EditInsuranceRequestDto dto = new EditInsuranceRequestDto();
        dto.setPatientId(1L);
        dto.setInsuranceYear(2023);
        dto.setInsuranceMonth(6);
        dto.setDateOfPayment(LocalDate.of(2023, 6, 10));
        dto.setSum(150.0);

        Patient patient = createSamplePatient(1L, "John Doe");
        Insurance existingInsurance = createSampleInsurance(1L, patient, LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 15), 100.0);

        when(insuranceRepository.findById(1L)).thenReturn(Optional.of(existingInsurance));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(insuranceRepository.existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 6, 1), 1L)).thenReturn(false);
        when(insuranceRepository.save(any(Insurance.class))).thenThrow(new RuntimeException("DB Error"));

        Insurance updatedInsurance = insuranceService.update(1L, dto, bindingResult);

        assertNull(updatedInsurance);
        verify(insuranceRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(insuranceRepository, times(1)).existsByPatientIdAndInsuranceDateAndIdNot(1L, LocalDate.of(2023, 6, 1), 1L);
        verify(insuranceRepository, times(1)).save(any(Insurance.class));
        verify(bindingResult, times(1)).rejectValue("date", "error.insurance", "Грешка при редактиране на осигуровка");
    }
}
