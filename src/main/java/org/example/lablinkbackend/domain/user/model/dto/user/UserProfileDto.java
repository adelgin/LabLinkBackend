package org.example.lablinkbackend.domain.user.model.dto.user;

import lombok.Data;
import org.example.lablinkbackend.domain.career.model.dto.CareerDto;
import org.example.lablinkbackend.domain.education.model.dto.EducationDto;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String patronymic;
    private LocalDate birthDate;
    private String cityName;
    private String countryName;

    private Set<String> tags;
    private List<EducationDto> education;
    private List<CareerDto> career;
    private List<PublicationResponseDto> publications;

    private boolean isMyProfile;
}
