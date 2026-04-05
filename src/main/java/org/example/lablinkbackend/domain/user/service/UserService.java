package org.example.lablinkbackend.domain.user.service;

import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.career.model.dto.CareerDto;
import org.example.lablinkbackend.domain.education.model.dto.EducationDto;
import org.example.lablinkbackend.domain.publication.model.dto.AuthorDto;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationAuthor;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationResponseDto;
import org.example.lablinkbackend.domain.tags.model.entity.Tag;
import org.example.lablinkbackend.domain.user.model.dto.user.UserProfileDto;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
           String.format("User '%s' not found", username)
        ));

        return UserDetailsImpl.build(user);
    }

    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String currentAuthUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPatronymic(user.getPatronymic());
        dto.setBirthDate(user.getBirthDate());

        dto.setMyProfile(user.getUsername().equals(currentAuthUsername));

        if (user.getCity() != null) {
            dto.setCityName(user.getCity().getName());
            dto.setCountryName(user.getCity().getCountry().getName());
        }

        dto.setTags(user.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));

        if (user.getEducation() != null) {
            List<EducationDto> eduDtos = user.getEducation().stream().map(edu -> {
                EducationDto eduDto = new EducationDto();
                eduDto.setOrganizationId(edu.getOrganization().getId());
                eduDto.setOrganizationName(edu.getOrganization().getName());
                eduDto.setFieldOfStudy(edu.getFieldOfStudy());
                eduDto.setDegree(edu.getDegree());
                eduDto.setStartDate(edu.getStartDate());
                eduDto.setEndDate(edu.getEndDate());
                return eduDto;
            }).collect(Collectors.toList());
            dto.setEducation(eduDtos);
        }

        if (user.getCareer() != null) {
            List<CareerDto> careerDtos = user.getCareer().stream().map(career -> {
                CareerDto carDto = new CareerDto();
                carDto.setOrganizationId(career.getOrganization().getId());
                carDto.setOrganizationName(career.getOrganization().getName());
                carDto.setTitle(career.getTitle());
                carDto.setStartDate(career.getStartDate());
                carDto.setEndDate(career.getEndDate());
                carDto.setIsCurrent(career.isCurrent());
                carDto.setDescription(career.getDescription());
                carDto.setDepartment(career.getDepartment());
                return carDto;
            }).collect(Collectors.toList());
            dto.setCareer(careerDtos);
        }

        if (user.getAuthorPublications() != null) {
            List<PublicationResponseDto> publicationDtos = user.getAuthorPublications().stream()
                .map(publication -> {
                PublicationResponseDto pubDto = new PublicationResponseDto();
                pubDto.setId(publication.getId());
                pubDto.setDoi(publication.getDoi());
                pubDto.setJournalTitle(publication.getJournalTitle());
                pubDto.setSource(publication.getSource());
                pubDto.setTitle(publication.getTitle());
                pubDto.setDescription(publication.getDescription());
                pubDto.setUrl(publication.getUrl());
                pubDto.setYear(publication.getYear());

                if (publication.getAuthors() != null) {
                    List<AuthorDto> authorDtos = publication.getAuthors().stream()
                            .map(publicationAuthor -> {
                                AuthorDto authorDto = new AuthorDto();
                                User authorUser = publicationAuthor.getUser();
                                if (authorUser != null) {
                                    authorDto.setUserId(authorUser.getId());
                                    authorDto.setFirstName(authorUser.getFirstName());
                                    authorDto.setLastName(authorUser.getLastName());
                                    authorDto.setAuthorOrder(publicationAuthor.getAuthorOrder());
                                    authorDto.setIsCorrespondingAuthor(publicationAuthor.getIsCorrespondingAuthor());
                                }
                                authorDto.setAuthorOrder(publicationAuthor.getAuthorOrder());
                                authorDto.setIsCorrespondingAuthor(publicationAuthor.getIsCorrespondingAuthor());
                                return authorDto;
                            })
                            .collect(Collectors.toList());
                    pubDto.setAuthors(authorDtos);
                }

                return pubDto;
            })
            .toList();

            dto.setPublications(publicationDtos);
        }

        return dto;
    }
}
