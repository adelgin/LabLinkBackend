package org.example.lablinkbackend.domain.education.model.dto;

import lombok.Data;
import org.example.lablinkbackend.domain.education.model.enums.EducationLevel;

import java.time.LocalDate;

@Data
public class EducationDto {
    private Long organizationId;
    private EducationLevel level;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
}
