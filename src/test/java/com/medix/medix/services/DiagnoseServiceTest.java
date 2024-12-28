package com.medix.medix.services;

import com.medix.medix.dtos.diagnose.CreateDiagnoseRequestDto;
import com.medix.medix.dtos.diagnose.EditDiagnoseRequestDto;
import com.medix.medix.entities.Diagnose;
import com.medix.medix.repositories.DiagnoseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnoseServiceTest {
    @InjectMocks
    private DiagnoseService diagnoseService;

    @Mock
    private DiagnoseRepository diagnoseRepository;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Diagnose createSampleDiagnose(Long id, String name, String description) {
        Diagnose diagnose = new Diagnose();
        diagnose.setId(id);
        diagnose.setName(name);
        diagnose.setDescription(description);

        return diagnose;
    }

    @Test
    void create_ValidDto_DiagnoseCreated() {
        CreateDiagnoseRequestDto dto = new CreateDiagnoseRequestDto();
        dto.setName("Flu");
        dto.setDescription("Influenza");

        when(diagnoseRepository.save(any(Diagnose.class))).thenAnswer(invocation -> {
            Diagnose diagnose = invocation.getArgument(0);
            diagnose.setId(1L);
            return diagnose;
        });

        Diagnose createdDiagnose = diagnoseService.create(dto, bindingResult);

        assertNotNull(createdDiagnose);
        assertEquals(1L, createdDiagnose.getId());
        assertEquals("Flu", createdDiagnose.getName());
        assertEquals("Influenza", createdDiagnose.getDescription());
        verify(diagnoseRepository, times(1)).save(any(Diagnose.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void create_NullDto_BindingError() {
        Diagnose createdDiagnose = diagnoseService.create(null, bindingResult);

        assertNull(createdDiagnose);
        verify(bindingResult, times(1)).rejectValue("name", "error.diagnose", "Грешка при създаване на диагноза");
        verifyNoMoreInteractions(diagnoseRepository);
    }

    @Test
    void create_Exception_BindingError() {
        CreateDiagnoseRequestDto dto = new CreateDiagnoseRequestDto();
        dto.setName("Flu");
        dto.setDescription("Influenza");

        when(diagnoseRepository.save(any(Diagnose.class))).thenThrow(new RuntimeException("DB Error"));

        Diagnose createdDiagnose = diagnoseService.create(dto, bindingResult);

        assertNull(createdDiagnose);
        verify(diagnoseRepository, times(1)).save(any(Diagnose.class));
        verify(bindingResult, times(1)).rejectValue("name", "error.diagnose", "Грешка при създаване на диагноза");
    }

    @Test
    void update_ValidDto_DiagnoseUpdated() {
        Long diagnoseId = 1L;
        EditDiagnoseRequestDto dto = new EditDiagnoseRequestDto();
        dto.setName("Common Cold");
        dto.setDescription("Viral infection");

        Diagnose existingDiagnose = createSampleDiagnose(diagnoseId, "Flu", "Influenza");

        when(diagnoseRepository.findById(diagnoseId)).thenReturn(Optional.of(existingDiagnose));
        when(diagnoseRepository.save(any(Diagnose.class))).thenReturn(existingDiagnose);

        Diagnose updatedDiagnose = diagnoseService.update(diagnoseId, dto, bindingResult);

        assertNotNull(updatedDiagnose);
        assertEquals("Common Cold", updatedDiagnose.getName());
        assertEquals("Viral infection", updatedDiagnose.getDescription());
        verify(diagnoseRepository, times(1)).findById(diagnoseId);
        verify(diagnoseRepository, times(1)).save(existingDiagnose);
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void update_NullDto_BindingError() {
        Long diagnoseId = 1L;

        Diagnose updatedDiagnose = diagnoseService.update(diagnoseId, null, bindingResult);

        assertNull(updatedDiagnose);
        verify(bindingResult, times(1)).rejectValue("name", "error.diagnose", "Грешка при редактиране на диагноза");
        verifyNoMoreInteractions(diagnoseRepository);
    }

    @Test
    void update_DiagnoseNotFound_BindingError() {
        Long diagnoseId = 1L;
        EditDiagnoseRequestDto dto = new EditDiagnoseRequestDto();
        dto.setName("Common Cold");
        dto.setDescription("Viral infection");

        when(diagnoseRepository.findById(diagnoseId)).thenReturn(Optional.empty());

        Diagnose updatedDiagnose = diagnoseService.update(diagnoseId, dto, bindingResult);

        assertNull(updatedDiagnose);
        verify(diagnoseRepository, times(1)).findById(diagnoseId);
        verify(bindingResult, times(1)).rejectValue("name", "error.diagnose", "Диагнозата не съществува");
        verifyNoMoreInteractions(diagnoseRepository);
    }

    @Test
    void update_Exception_BindingError() {
        Long diagnoseId = 1L;
        EditDiagnoseRequestDto dto = new EditDiagnoseRequestDto();
        dto.setName("Common Cold");
        dto.setDescription("Viral infection");

        Diagnose existingDiagnose = createSampleDiagnose(diagnoseId, "Flu", "Influenza");

        when(diagnoseRepository.findById(diagnoseId)).thenReturn(Optional.of(existingDiagnose));
        when(diagnoseRepository.save(any(Diagnose.class))).thenThrow(new RuntimeException("DB Error"));

        Diagnose updatedDiagnose = diagnoseService.update(diagnoseId, dto, bindingResult);

        assertNull(updatedDiagnose);
        verify(diagnoseRepository, times(1)).findById(diagnoseId);
        verify(diagnoseRepository, times(1)).save(existingDiagnose);
        verify(bindingResult, times(1)).rejectValue("name", "error.diagnose", "Грешка при редактиране на диагноза");
    }

    @Test
    void findAllOrderedByName_ReturnsSortedDiagnoses() {
        List<Diagnose> diagnoses = Arrays.asList(
                createSampleDiagnose(1L, "Asthma", "Respiratory condition"),
                createSampleDiagnose(2L, "Bronchitis", "Inflammation of the bronchial tubes")
        );

        when(diagnoseRepository.findAllByOrderByName()).thenReturn(diagnoses);

        List<Diagnose> result = diagnoseService.findAllOrderedByName();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Asthma", result.get(0).getName());
        assertEquals("Bronchitis", result.get(1).getName());
        verify(diagnoseRepository, times(1)).findAllByOrderByName();
    }

    @Test
    void findById_DiagnoseExists_ReturnsDiagnose() {
        Long diagnoseId = 1L;
        Diagnose diagnose = createSampleDiagnose(diagnoseId, "Asthma", "Respiratory condition");

        when(diagnoseRepository.findById(diagnoseId)).thenReturn(Optional.of(diagnose));

        Diagnose foundDiagnose = diagnoseService.findById(diagnoseId);

        assertNotNull(foundDiagnose);
        assertEquals(diagnoseId, foundDiagnose.getId());
        verify(diagnoseRepository, times(1)).findById(diagnoseId);
    }

    @Test
    void findById_DiagnoseDoesNotExist_ThrowsException() {
        Long diagnoseId = 1L;
        when(diagnoseRepository.findById(diagnoseId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> diagnoseService.findById(diagnoseId));
        verify(diagnoseRepository, times(1)).findById(diagnoseId);
    }
}