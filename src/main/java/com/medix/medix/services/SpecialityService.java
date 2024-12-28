package com.medix.medix.services;

import com.medix.medix.dtos.speciality.EditSpecialityRequestDto;
import com.medix.medix.dtos.speciality.CreateSpecialityRequestDto;
import com.medix.medix.entities.Speciality;
import com.medix.medix.repositories.SpecialityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialityService {
    private final SpecialityRepository specialityRepository;

    public List<Speciality> findAll() {
        return specialityRepository.findAll();
    }

    public Page<Speciality> findAll(Pageable pageable) {
        return specialityRepository.findAll(pageable);
    }

    public Speciality create(CreateSpecialityRequestDto specialityRequestDto, BindingResult bindingResult) {
        if (specialityRequestDto == null) {
            bindingResult.rejectValue("name", "error.speciality", "Грешка при създаване на специалност");
            return null;
        }

        Speciality speciality = new Speciality();
        speciality.setName(specialityRequestDto.getName());

        try {
            return specialityRepository.save(speciality);
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.speciality", "Грешка при създаване на специалност");
            return null;
        }
    }

    public Speciality update(Long id, EditSpecialityRequestDto editSpecialityRequestDto, BindingResult bindingResult) {
        if (editSpecialityRequestDto == null) {
            bindingResult.rejectValue("name", "error.speciality", "Грешка при редактиране на специалност");
            return null;
        }

        Speciality speciality;
        try {
            speciality = findById(id);
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.speciality", "Специалността не съществува");
            return null;
        }

        speciality.setName(editSpecialityRequestDto.getName());

        try {
            return specialityRepository.save(speciality);
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.speciality", "Грешка при редактиране на специалност");
            return null;
        }
    }

    public Speciality findById(Long id) {
        return specialityRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        specialityRepository.deleteById(id);
    }
}
