package com.medix.medix.dtos.insurance;

import com.medix.medix.entities.Insurance;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EditInsuranceRequestDto {
    private Long id;

    @NotNull(message = "Пациентът е задължителен.")
    private Long patientId;

    @NotNull(message = "Месецът е задължителен.")
    @Range(min = 1, max = 12, message = "Месецът трябва да бъде между 1 и 12.")
    private Integer insuranceMonth;

    @NotNull(message = "Годината е задължителна.")
    private Integer insuranceYear;

    @NotNull(message = "Дата е задължителна.")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfPayment;

    @NotNull(message = "Сумата е задължителна.")
    @Min(value = 0, message = "Сумата не може да бъде отрицателно число.")
    private Double sum;

    public EditInsuranceRequestDto() {
    }

    public EditInsuranceRequestDto(Insurance insurance) {
        id = insurance.getId();
        patientId = insurance.getPatient().getId();
        insuranceMonth = insurance.getInsuranceDate().getMonthValue();
        insuranceYear = insurance.getInsuranceDate().getYear();
        dateOfPayment = insurance.getDateOfPayment();
        sum = insurance.getSum();
    }

    public LocalDate getInsuranceDate() {
        return LocalDate.of(insuranceYear, insuranceMonth, 1);
    }
}