package org.example.lablinkbackend.config.security.service;

import jakarta.transaction.Transactional;
import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.config.security.component.JwtCore;
import org.example.lablinkbackend.domain.career.model.entity.CareerExperience;
import org.example.lablinkbackend.domain.career.model.repository.CareerRepository;
import org.example.lablinkbackend.domain.education.model.entity.Education;
import org.example.lablinkbackend.domain.education.repository.EducationRepository;
import org.example.lablinkbackend.domain.geo.repository.CityRepository;
import org.example.lablinkbackend.domain.organization.repository.OrganizationRepository;
import org.example.lablinkbackend.domain.publication.model.dto.Publication;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationAuthor;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationRequestDto;
import org.example.lablinkbackend.domain.publication.repository.PublicationAuthorRepository;
import org.example.lablinkbackend.domain.publication.repository.PublicationsRepository;
import org.example.lablinkbackend.domain.tags.model.entity.Tag;
import org.example.lablinkbackend.domain.tags.repository.TagRepository;
import org.example.lablinkbackend.domain.user.model.dto.auth.SignUpRequest;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;
    private final CityRepository cityRepository;
    private final OrganizationRepository organizationRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final PublicationsRepository publicationRepository;
    private final PublicationAuthorRepository publicationAuthorRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TagRepository tagRepository,
            CityRepository cityRepository,
            OrganizationRepository organizationRepository,
            EducationRepository educationRepository,
            CareerRepository careerRepository,
            PublicationsRepository publicationRepository,
            PublicationAuthorRepository publicationAuthorRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tagRepository = tagRepository;
        this.cityRepository = cityRepository;
        this.organizationRepository = organizationRepository;
        this.educationRepository = educationRepository;
        this.careerRepository = careerRepository;
        this.publicationRepository = publicationRepository;
        this.publicationAuthorRepository = publicationAuthorRepository;
    }

    @Transactional
    public User register(SignUpRequest signUpRequest) {
        try {
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setPatronymic(signUpRequest.getPatronymic());
            user.setBirthDate(signUpRequest.getBirthDate());

            if (signUpRequest.getCityId() != null) {
                user.setCity(cityRepository.findById(Long.valueOf(signUpRequest.getCityId()))
                        .orElseThrow(() -> new ResourceNotFoundException("City with ID " + signUpRequest.getCityId() + " not found")));
            }

            if (signUpRequest.getTags() != null && !signUpRequest.getTags().isEmpty()) {
                Set<Tag> userTags = signUpRequest.getTags().stream()
                        .map(tagName -> tagRepository.findByName(tagName.toLowerCase())
                                .orElseGet(() -> {
                                    Tag newTag = new Tag();
                                    newTag.setName(tagName.toLowerCase());
                                    return tagRepository.save(newTag);
                                }))
                        .collect(Collectors.toSet());
                user.setTags(userTags);
            }

            User savedUser = userRepository.save(user);

            if (signUpRequest.getEducation() != null) {
                signUpRequest.getEducation().forEach(eduDto -> {
                    Education edu = new Education();
                    edu.setUser(savedUser);
                    edu.setFieldOfStudy(eduDto.getFieldOfStudy());
                    edu.setDegree(eduDto.getDegree());
                    edu.setStartDate(eduDto.getStartDate());
                    edu.setEndDate(eduDto.getEndDate());
                    edu.setOrganization(organizationRepository.findById(eduDto.getOrganizationId())
                            .orElseThrow(() -> new RuntimeException("Organization (Education) not found")));
                    educationRepository.save(edu);
                });
            }

            if (signUpRequest.getCareer() != null) {
                signUpRequest.getCareer().forEach(carDto -> {
                    CareerExperience career = new CareerExperience();
                    career.setUser(savedUser);
                    career.setTitle(carDto.getTitle());
                    career.setStartDate(carDto.getStartDate());
                    career.setEndDate(carDto.getEndDate());
                    career.setCurrent(carDto.getIsCurrent());
                    career.setOrganization(organizationRepository.findById(carDto.getOrganizationId())
                            .orElseThrow(() -> new RuntimeException("Organization (Career) not found")));
                    careerRepository.save(career);
                });
            }

            if (signUpRequest.getPublications() != null && !signUpRequest.getPublications().isEmpty()) {
                for (PublicationRequestDto pubDto : signUpRequest.getPublications()) {
                    Publication publication = new Publication();
                    publication.setDoi(pubDto.getDoi());
                    publication.setJournalTitle(pubDto.getJournalTitle());
                    publication.setSource(pubDto.getSource());
                    publication.setTitle(pubDto.getTitle());
                    publication.setDescription(pubDto.getDescription());
                    publication.setUrl(pubDto.getUrl());
                    publication.setYear(pubDto.getYear());

                    Publication savedPublication = publicationRepository.save(publication);

                    PublicationAuthor mainAuthor = new PublicationAuthor();
                    mainAuthor.setPublication(savedPublication);
                    mainAuthor.setUser(savedUser);
                    mainAuthor.setAuthorOrder(1);
                    mainAuthor.setIsCorrespondingAuthor(true);
                    publicationAuthorRepository.save(mainAuthor);

                    if (pubDto.getCoAuthors() != null && !pubDto.getCoAuthors().isEmpty()) {
                        for (PublicationRequestDto.CoAuthorDto coAuthorDto : pubDto.getCoAuthors()) {
                            User coAuthor = userRepository.findById(coAuthorDto.getUserId())
                                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + coAuthorDto.getUserId() + " not found"));

                            PublicationAuthor coAuthorLink = new PublicationAuthor();
                            coAuthorLink.setPublication(savedPublication);
                            coAuthorLink.setUser(coAuthor);
                            coAuthorLink.setAuthorOrder(coAuthorDto.getAuthorOrder());
                            coAuthorLink.setIsCorrespondingAuthor(coAuthorDto.getIsCorrespondingAuthor());
                            publicationAuthorRepository.save(coAuthorLink);
                        }
                    }
                }
            }

            return savedUser;

        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }
}