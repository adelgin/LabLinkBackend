package org.example.lablinkbackend.domain.user.model.dto.auth;

import lombok.Data;
import org.example.lablinkbackend.domain.career.model.dto.CareerDto;
import org.example.lablinkbackend.domain.education.model.dto.EducationDto;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private LocalDate birthDate;
    private Integer cityId;

    private List<EducationDto> education;
    private List<CareerDto> career;
    private Set<String> tags;
    private List<PublicationRequestDto> publications;
}