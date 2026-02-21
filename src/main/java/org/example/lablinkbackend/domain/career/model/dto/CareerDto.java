package org.example.lablinkbackend.domain.career.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CareerDto {
    private Long organizationId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
}
