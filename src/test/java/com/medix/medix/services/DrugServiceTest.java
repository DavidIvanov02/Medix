package com.medix.medix.services;

import com.medix.medix.dtos.drug.CreateDrugRequestDto;
import com.medix.medix.dtos.drug.EditDrugRequestDto;
import com.medix.medix.entities.Appointment;
import com.medix.medix.entities.Drug;
import com.medix.medix.repositories.DrugRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrugServiceTest {
    @InjectMocks
    private DrugService drugService;

    @Mock
    private DrugRepository drugRepository;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Drug createSampleDrug(Long id, String name, String description, Double price) {
        Drug drug = new Drug();
        drug.setId(id);
        drug.setName(name);
        drug.setDescription(description);
        drug.setPrice(price);

        return drug;
    }

    @Test
    void create_ValidDto_DrugCreated() {
        CreateDrugRequestDto dto = new CreateDrugRequestDto();
        dto.setName("Aspirin");
        dto.setDescription("Pain reliever");
        dto.setPrice(9.99);

        when(drugRepository.save(any(Drug.class))).thenAnswer(invocation -> {
            Drug drug = invocation.getArgument(0);
            drug.setId(1L);
            return drug;
        });

        Drug createdDrug = drugService.create(dto, bindingResult);

        assertNotNull(createdDrug);
        assertEquals(1L, createdDrug.getId());
        assertEquals("Aspirin", createdDrug.getName());
        assertEquals("Pain reliever", createdDrug.getDescription());
        assertEquals(9.99, createdDrug.getPrice());
        verify(drugRepository, times(1)).save(any(Drug.class));
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void create_NullDto_BindingError() {
        Drug createdDrug = drugService.create(null, bindingResult);

        assertNull(createdDrug);
        verify(bindingResult, times(1)).rejectValue("name", "error.drug", "Грешка при създаване на лекарство");
        verifyNoMoreInteractions(drugRepository);
    }

    @Test
    void create_Exception_BindingError() {
        CreateDrugRequestDto dto = new CreateDrugRequestDto();
        dto.setName("Ibuprofen");
        dto.setDescription("Anti-inflammatory");
        dto.setPrice(14.99);

        when(drugRepository.save(any(Drug.class))).thenThrow(new RuntimeException("DB Error"));

        Drug createdDrug = drugService.create(dto, bindingResult);

        assertNull(createdDrug);
        verify(drugRepository, times(1)).save(any(Drug.class));
        verify(bindingResult, times(1)).rejectValue("name", "error.drug", "Грешка при създаване на лекарство");
    }

    @Test
    void update_ValidDto_DrugUpdated() {
        Long drugId = 1L;
        EditDrugRequestDto dto = new EditDrugRequestDto();
        dto.setName("Paracetamol");
        dto.setDescription("Fever reducer");
        dto.setPrice(12.99);

        Drug existingDrug = createSampleDrug(drugId, "Aspirin", "Pain reliever", 9.99);

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(existingDrug));
        when(drugRepository.save(any(Drug.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Drug updatedDrug = drugService.update(drugId, dto, bindingResult);

        assertNotNull(updatedDrug);
        assertEquals("Paracetamol", updatedDrug.getName());
        assertEquals("Fever reducer", updatedDrug.getDescription());
        assertEquals(12.99, updatedDrug.getPrice());
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugRepository, times(1)).save(existingDrug);
        verifyNoMoreInteractions(bindingResult);
    }

    @Test
    void update_NullDto_BindingError() {
        Long drugId = 1L;

        Drug updatedDrug = drugService.update(drugId, null, bindingResult);

        assertNull(updatedDrug);
        verify(bindingResult, times(1)).rejectValue("name", "error.drug", "Грешка при редактиране на лекарство");
        verifyNoMoreInteractions(drugRepository);
    }

    @Test
    void update_DrugNotFound_BindingError() {
        Long drugId = 99L;
        EditDrugRequestDto dto = new EditDrugRequestDto();
        dto.setName("NonExistentDrug");
        dto.setDescription("Does not exist");
        dto.setPrice(19.99);

        when(drugRepository.findById(drugId)).thenReturn(Optional.empty());

        Drug updatedDrug = drugService.update(drugId, dto, bindingResult);

        assertNull(updatedDrug);
        verify(drugRepository, times(1)).findById(drugId);
        verify(bindingResult, times(1)).rejectValue("name", "error.drug", "Лекарството не съществува");
        verifyNoMoreInteractions(drugRepository);
    }

    @Test
    void update_Exception_BindingError() {
        Long drugId = 1L;
        EditDrugRequestDto dto = new EditDrugRequestDto();
        dto.setName("Ibuprofen");
        dto.setDescription("Anti-inflammatory");
        dto.setPrice(14.99);

        Drug existingDrug = createSampleDrug(drugId, "Aspirin", "Pain reliever", 9.99);

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(existingDrug));
        when(drugRepository.save(any(Drug.class))).thenThrow(new RuntimeException("DB Error"));

        Drug updatedDrug = drugService.update(drugId, dto, bindingResult);

        assertNull(updatedDrug);
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugRepository, times(1)).save(existingDrug);
        verify(bindingResult, times(1)).rejectValue("name", "error.drug", "Грешка при редактиране на лекарство");
    }

    @Test
    void findById_DrugExists_ReturnsDrug() {
        Long drugId = 1L;
        Drug drug = createSampleDrug(drugId, "Aspirin", "Pain reliever", 9.99);

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));

        Drug foundDrug = drugService.findById(drugId);

        assertNotNull(foundDrug);
        assertEquals(drugId, foundDrug.getId());
        verify(drugRepository, times(1)).findById(drugId);
    }

    @Test
    void findById_DrugDoesNotExist_ThrowsException() {
        Long drugId = 1L;
        when(drugRepository.findById(drugId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> drugService.findById(drugId));
        verify(drugRepository, times(1)).findById(drugId);
    }

    @Test
    void deleteById_DrugExistsAndNotUsed_DeletesDrug() throws Exception {
        Long drugId = 1L;
        Drug drug = createSampleDrug(drugId, "Aspirin", "Pain reliever", 9.99);
        drug.setAppointments(Arrays.asList());

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));
        doNothing().when(drugRepository).deleteById(drugId);

        drugService.deleteById(drugId);

        verify(drugRepository, times(1)).findById(drugId);
        verify(drugRepository, times(1)).deleteById(drugId);
    }

    @Test
    void deleteById_DrugUsedInMedicalAppointments_ThrowsException() {
        Long drugId = 1L;
        Drug drug = createSampleDrug(drugId, "Aspirin", "Pain reliever", 9.99);
        drug.setAppointments(Arrays.asList(new Appointment()));

        when(drugRepository.findById(drugId)).thenReturn(Optional.of(drug));

        Exception exception = assertThrows(Exception.class, () -> drugService.deleteById(drugId));
        assertEquals("Лекарството се използва в медицински записи", exception.getMessage());
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteById_DrugNotFound_ThrowsException() {
        Long drugId = 99L;
        when(drugRepository.findById(drugId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> drugService.findById(drugId));
        verify(drugRepository, times(1)).findById(drugId);
    }
}
