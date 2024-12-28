package com.medix.medix.services;

import com.medix.medix.dtos.appointment.EditAppointmentRequestDto;
import com.medix.medix.dtos.appointment.CreateAppointmentRequestDto;
import com.medix.medix.entities.*;
import com.medix.medix.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DiagnoseRepository diagnoseRepository;

    @Mock
    private DrugRepository drugRepository;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Appointment createSampleAppointment(Long id, LocalDate date, Doctor doctor, Patient patient, Diagnose diagnose, List<Drug> drugs) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setDate(date);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnose(diagnose);
        appointment.setDrugs(new ArrayList<>(drugs));

        return appointment;
    }

    private Doctor createSampleDoctor(Long id, String name) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setUsername(name);
        return doctor;
    }

    private Patient createSamplePatient(Long id, String name) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setUsername(name);
        return patient;
    }

    private Diagnose createSampleDiagnose(Long id, String name) {
        Diagnose diagnose = new Diagnose();
        diagnose.setId(id);
        diagnose.setName(name);
        return diagnose;
    }

    private Drug createSampleDrug(Long id, String name) {
        Drug drug = new Drug();
        drug.setId(id);
        drug.setName(name);
        return drug;
    }

    @Test
    void create_ValidDto_AppointmentCreated() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L, 2L));

        Doctor doctor = createSampleDoctor(1L, "Dr. Smith");
        Patient patient = createSamplePatient(1L, "John Doe");
        Diagnose diagnose = createSampleDiagnose(1L, "Flu");
        Drug drug1 = createSampleDrug(1L, "DrugA");
        Drug drug2 = createSampleDrug(2L, "DrugB");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(1L)).thenReturn(Optional.of(drug1));
        when(drugRepository.findById(2L)).thenReturn(Optional.of(drug2));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(1L);
            return appointment;
        });

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNotNull(createdAppointment);
        assertEquals(1L, createdAppointment.getId());
        assertEquals(LocalDate.of(2023, 7, 20), createdAppointment.getDate());
        assertEquals(doctor, createdAppointment.getDoctor());
        assertEquals(patient, createdAppointment.getPatient());
        assertEquals(diagnose, createdAppointment.getDiagnose());
        assertEquals(2, createdAppointment.getDrugs().size());
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(drugRepository, times(1)).findById(1L);
        verify(drugRepository, times(1)).findById(2L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void create_NullDto_BindingError() {
        Appointment createdAppointment = appointmentService.create(null, bindingResult);

        assertNull(createdAppointment);
        verify(bindingResult, times(1)).rejectValue("date", "error.appointment", "Грешка при създаване на специалност");
        verifyNoMoreInteractions(doctorRepository, patientRepository, diagnoseRepository, drugRepository, appointmentRepository);
    }

    @Test
    void create_DoctorNotFound_BindingError() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L));

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNull(createdAppointment);
        verify(doctorRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("doctorId", "error.appointment", "Докторът не съществува");
        verifyNoMoreInteractions(patientRepository, diagnoseRepository, drugRepository, appointmentRepository);
    }

    @Test
    void create_PatientNotFound_BindingError() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L));

        Doctor doctor = createSampleDoctor(1L, "Dr. Smith");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNull(createdAppointment);
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("patientId", "error.appointment", "Пациентът не съществува");
        verifyNoMoreInteractions(diagnoseRepository, drugRepository, appointmentRepository);
    }

    @Test
    void create_DiagnoseNotFound_BindingError() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L));

        Doctor doctor = createSampleDoctor(1L, "Dr. Smith");
        Patient patient = createSamplePatient(1L, "John Doe");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.empty());

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNull(createdAppointment);
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("diagnoseId", "error.appointment", "Диагнозата не съществува");
        verifyNoMoreInteractions(drugRepository, appointmentRepository);
    }

    @Test
    void create_DrugNotFound_BindingError() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L, 99L));

        Doctor doctor = createSampleDoctor(1L, "Dr. Smith");
        Patient patient = createSamplePatient(1L, "John Doe");
        Diagnose diagnose = createSampleDiagnose(1L, "Flu");
        Drug drug1 = createSampleDrug(1L, "DrugA");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(1L)).thenReturn(Optional.of(drug1));
        when(drugRepository.findById(99L)).thenReturn(Optional.empty());

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNull(createdAppointment);
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(drugRepository, times(1)).findById(1L);
        verify(drugRepository, times(1)).findById(99L);
        verify(bindingResult, times(1)).rejectValue("specialities", "error.appointment", "Грешка при добавяне на лекарства");
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    void create_Exception_BindingError() {
        CreateAppointmentRequestDto dto = new CreateAppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnoseId(1L);
        dto.setDate(LocalDate.of(2023, 7, 20));
        dto.setDrugs(Arrays.asList(1L));

        Doctor doctor = createSampleDoctor(1L, "Dr. Smith");
        Patient patient = createSamplePatient(1L, "John Doe");
        Diagnose diagnose = createSampleDiagnose(1L, "Flu");
        Drug drug1 = createSampleDrug(1L, "DrugA");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(1L)).thenReturn(Optional.of(drug1));
        when(appointmentRepository.save(any(Appointment.class))).thenThrow(new RuntimeException("DB Error"));

        Appointment createdAppointment = appointmentService.create(dto, bindingResult);

        assertNull(createdAppointment);
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(drugRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(bindingResult, times(1)).rejectValue("date", "error.appointment", "Грешка при създаване на специалност");
    }

    @Test
    void update_ValidDto_AppointmentUpdated() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L, 3L));

        Doctor doctor = createSampleDoctor(2L, "Dr. Johnson");
        Patient patient = createSamplePatient(2L, "Jane Doe");
        Diagnose diagnose = createSampleDiagnose(2L, "Cold");
        Drug drug2 = createSampleDrug(2L, "DrugB");
        Drug drug3 = createSampleDrug(3L, "DrugC");

        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(2L)).thenReturn(Optional.of(drug2));
        when(drugRepository.findById(3L)).thenReturn(Optional.of(drug3));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNotNull(updatedAppointment);
        assertEquals(1L, updatedAppointment.getId());
        assertEquals(LocalDate.of(2023, 8, 25), updatedAppointment.getDate());
        assertEquals(doctor, updatedAppointment.getDoctor());
        assertEquals(patient, updatedAppointment.getPatient());
        assertEquals(diagnose, updatedAppointment.getDiagnose());
        assertEquals(2, updatedAppointment.getDrugs().size());
        verify(appointmentRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
        verify(patientRepository, times(1)).findById(2L);
        verify(diagnoseRepository, times(1)).findById(2L);
        verify(drugRepository, times(1)).findById(2L);
        verify(drugRepository, times(1)).findById(3L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void update_NullDto_BindingError() {
        Appointment updatedAppointment = appointmentService.update(1L, null, bindingResult);

        assertNull(updatedAppointment);
        verify(bindingResult, times(1)).rejectValue("date", "error.appointment", "Грешка при редактиране на специалност");
        verifyNoMoreInteractions(appointmentRepository, doctorRepository, patientRepository, diagnoseRepository, drugRepository);
    }

    @Test
    void update_AppointmentNotFound_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(bindingResult, times(1)).rejectValue("date", "error.appointment", "Специалността не съществува");
        verifyNoMoreInteractions(doctorRepository, patientRepository, diagnoseRepository, drugRepository);
    }

    @Test
    void update_DoctorNotFound_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L));

        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
        verify(bindingResult, times(1)).rejectValue("doctorId", "error.appointment", "Докторът не съществува");
        verifyNoMoreInteractions(patientRepository, diagnoseRepository, drugRepository);
    }

    @Test
    void update_PatientNotFound_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L));

        Doctor doctor = createSampleDoctor(2L, "Dr. Johnson");
        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(2L);
        verify(bindingResult, times(1)).rejectValue("patientId", "error.appointment", "Пациентът не съществува");
        verifyNoMoreInteractions(diagnoseRepository, drugRepository);
    }

    @Test
    void update_DiagnoseNotFound_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L));

        Doctor doctor = createSampleDoctor(2L, "Dr. Johnson");
        Patient patient = createSamplePatient(2L, "Jane Doe");
        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.empty());

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).findById(2L);
        verify(bindingResult, times(1)).rejectValue("diagnoseId", "error.appointment", "Диагнозата не съществува");
        verifyNoMoreInteractions(drugRepository);
    }

    @Test
    void update_DrugNotFound_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L, 99L));

        Doctor doctor = createSampleDoctor(2L, "Dr. Johnson");
        Patient patient = createSamplePatient(2L, "Jane Doe");
        Diagnose diagnose = createSampleDiagnose(2L, "Cold");
        Drug drug2 = createSampleDrug(2L, "DrugB");

        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(2L)).thenReturn(Optional.of(drug2));
        when(drugRepository.findById(99L)).thenReturn(Optional.empty());

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
        verify(patientRepository, times(1)).findById(2L);
        verify(diagnoseRepository, times(1)).findById(2L);
        verify(drugRepository, times(1)).findById(2L);
        verify(drugRepository, times(1)).findById(99L);
        verify(bindingResult, times(1)).rejectValue("drugs", "error.appointment", "Грешка при добавяне на лекарства");
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    void update_Exception_BindingError() {
        EditAppointmentRequestDto dto = new EditAppointmentRequestDto();
        dto.setDoctorId(2L);
        dto.setPatientId(2L);
        dto.setDiagnoseId(2L);
        dto.setDate(LocalDate.of(2023, 8, 25));
        dto.setDrugs(Arrays.asList(2L));

        Doctor doctor = createSampleDoctor(2L, "Dr. Johnson");
        Patient patient = createSamplePatient(2L, "Jane Doe");
        Diagnose diagnose = createSampleDiagnose(2L, "Cold");
        Drug drug2 = createSampleDrug(2L, "DrugB");

        Appointment existingAppointment = createSampleAppointment(1L, LocalDate.of(2023, 7, 20), createSampleDoctor(1L, "Dr. Smith"), createSamplePatient(1L, "John Doe"), createSampleDiagnose(1L, "Flu"), Arrays.asList(createSampleDrug(1L, "DrugA")));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.of(diagnose));
        when(drugRepository.findById(2L)).thenReturn(Optional.of(drug2));
        when(appointmentRepository.save(any(Appointment.class))).thenThrow(new RuntimeException("DB Error"));

        Appointment updatedAppointment = appointmentService.update(1L, dto, bindingResult);

        assertNull(updatedAppointment);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
        verify(patientRepository, times(1)).findById(2L);
        verify(diagnoseRepository, times(1)).findById(2L);
        verify(drugRepository, times(1)).findById(2L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(bindingResult, times(1)).rejectValue("date", "error.appointment", "Грешка при редактиране на специалност");
    }
}
