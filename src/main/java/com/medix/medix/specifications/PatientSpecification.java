package com.medix.medix.specifications;

import com.medix.medix.entities.Patient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecification {
    public static Specification<Patient> hasNameLike(String name) {
        return (Root<Patient> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + name.trim() + "%";

            return cb.or(
                    cb.like(root.get("firstName"), pattern),
                    cb.like(root.get("lastName"), pattern),
                    cb.like(root.get("username"), pattern)
            );
        };
    }

    public static Specification<Patient> hasDoctorId(Long doctorId) {
        return (Root<Patient> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (doctorId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("generalPractitioner").get("id"), doctorId);
        };
    }
}
