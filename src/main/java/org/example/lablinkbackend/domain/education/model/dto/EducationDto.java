package org.example.lablinkbackend.domain.education.model.dto;

import lombok.Data;
import org.example.lablinkbackend.domain.education.model.enums.EducationLevel;

import java.time.LocalDate;

@Data
public class EducationDto {
    private Long organizationId;
    private String organizationName;
    private EducationLevel degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
}
