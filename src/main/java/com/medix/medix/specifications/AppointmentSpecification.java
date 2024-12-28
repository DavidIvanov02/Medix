package com.medix.medix.specifications;

import com.medix.medix.entities.Appointment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AppointmentSpecification {
    public static Specification<Appointment> hasDiagnoseId(Long diagnoseId) {
        return (Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (diagnoseId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("diagnose").get("id"), diagnoseId);
        };
    }

    public static Specification<Appointment> hasDoctorId(Long doctorId) {
        return (Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (doctorId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("doctor").get("id"), doctorId);
        };
    }

    public static Specification<Appointment> hasPatientId(Long patientId) {
        return (Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (patientId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("patient").get("id"), patientId);
        };
    }

    public static Specification<Appointment> hasStartDateOnOrAfter(LocalDate startDate) {
        return (Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (startDate == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(root.get("date"), startDate);
        };
    }

    public static Specification<Appointment> hasEndDateOnOrBefore(LocalDate endDate) {
        return (Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (endDate == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }
}
