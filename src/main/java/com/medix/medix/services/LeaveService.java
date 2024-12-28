package com.medix.medix.services;

import com.medix.medix.dtos.leave.EditLeaveRequestDto;
import com.medix.medix.dtos.leave.CreateLeaveRequestDto;
import com.medix.medix.entities.Appointment;
import com.medix.medix.entities.Leave;
import com.medix.medix.entities.User;
import com.medix.medix.repositories.AppointmentRepository;
import com.medix.medix.repositories.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final AppointmentRepository appointmentRepository;
    private final LeaveRepository leaveRepository;

    public Page<Leave> findAllBasedOnRole(Pageable pageable) {
        User user = UserServiceImpl.getCurrentUser();

        if (user.isAdmin()) {
            return leaveRepository.findAll(pageable);
        }

        if (user.isDoctor()) {
            return leaveRepository.findAllByAppointment_DoctorId(user.getId(), pageable);
        }

        return leaveRepository.findAllByAppointment_PatientId(user.getId(), pageable);
    }

    public Leave create(CreateLeaveRequestDto leaveDto, BindingResult bindingResult) {
        if (leaveDto == null) {
            bindingResult.rejectValue("days", "error.leave", "Грешка при създаване на болничен лист");
            return null;
        }

        Appointment appointment;
        try {
            appointment = appointmentRepository.findById(leaveDto.getAppointmentId()).orElseThrow();
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът не съществува");
            return null;
        }

        User user = UserServiceImpl.getCurrentUser();

        if (user.isDoctor() && !appointment.getDoctor().getId().equals(user.getId())) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът не е направен от лекаря");
            return null;
        }

        if (leaveDto.getStartDate().isBefore(appointment.getDate())) {
            bindingResult.rejectValue("startDate", "error.leave", "Дата на болничен лист не може да бъде преди датата на прегледа");
            return null;
        }

        if (appointment.getLeave() != null) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът вече има болничен лист");
            return null;
        }

        Leave leave = new Leave();
        leave.setAppointment(appointment);
        leave.setStartDate(leaveDto.getStartDate());
        leave.setDays(leaveDto.getDays());

        try {
            return leaveRepository.save(leave);
        } catch (Exception e) {
            bindingResult.rejectValue("days", "error.leave", "Грешка при създаване на болничен лист");
            return null;
        }
    }

    public Leave update(Long id, EditLeaveRequestDto editLeaveDto, BindingResult bindingResult) {
        if (editLeaveDto == null) {
            bindingResult.rejectValue("days", "error.leave", "Грешка при редактиране на болничен лист");
            return null;
        }

        Leave leave;
        try {
            leave = findById(id);
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("days", "error.leave", "Болничният лист не съществува");
            return null;
        }

        Appointment appointment;
        try {
            appointment = appointmentRepository.findById(editLeaveDto.getAppointmentId()).orElseThrow();
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът не съществува");
            return null;
        }

        User user = UserServiceImpl.getCurrentUser();

        if (user.isDoctor() && !appointment.getDoctor().getId().equals(user.getId())) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът не е направен от лекаря");
            return null;
        }

        if (appointment.getLeave() != null && !appointment.getLeave().getId().equals(id)) {
            bindingResult.rejectValue("appointmentId", "error.leave", "Прегледът вече има болничен лист");
            return null;
        }

        leave.setAppointment(appointment);
        leave.setStartDate(editLeaveDto.getStartDate());
        leave.setDays(editLeaveDto.getDays());

        try {
            return leaveRepository.save(leave);
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.leave", "Грешка при редактиране на болничен лист");
            return null;
        }
    }

    public Leave findById(Long id) {
        return leaveRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        leaveRepository.deleteById(id);
    }

    public boolean isLeaveAppointmentFromDoctor(Long leaveId, Long doctorId) {
        Leave leave;
        try {
            leave = findById(leaveId);
        } catch (NoSuchElementException e) {
            return false;
        }

        return leave.getAppointment().getDoctor().getId().equals(doctorId);
    }
}
