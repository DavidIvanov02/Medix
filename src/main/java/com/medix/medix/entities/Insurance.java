package com.medix.medix.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "insurances")
@Getter
@Setter
public class Insurance extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Пациентът е задължителен")
    private Patient patient;

    @Column(name = "insurance_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Дата на осигуровката е задължителна")
    @DateTimeFormat(pattern = "yyyy-MM")
    private LocalDate insuranceDate;

    @Column(name = "date_of_payment", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Дата е задължителна")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfPayment;

    @Column(name = "sum", nullable = false)
    @NotNull(message = "Сумата е задължителна")
    @Min(value = 0, message = "Сумата не може да бъде отрицателно число")
    private Double sum;

    @Formula("YEAR(insurance_date)")
    private Integer insuranceMonth;

    @Formula("MONTH(insurance_date)")
    private Integer insuranceYear;
}
