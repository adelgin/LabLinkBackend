package org.example.lablinkbackend.domain.user.service;

import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.career.model.dto.CareerDto;
import org.example.lablinkbackend.domain.education.model.dto.EducationDto;
import org.example.lablinkbackend.domain.feed.model.dto.FeedContextDto;
import org.example.lablinkbackend.domain.publication.model.dto.AuthorDto;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationAuthor;
import org.example.lablinkbackend.domain.publication.model.dto.PublicationResponseDto;
import org.example.lablinkbackend.domain.tags.model.entity.Tag;
import org.example.lablinkbackend.domain.user.model.dto.user.UserCardDto;
import org.example.lablinkbackend.domain.user.model.dto.user.UserProfileDto;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
        dto.setId(user.getId());
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

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Получает контекст для формирования ленты (подписки, теги, группы)
     */
    @Transactional(readOnly = true)
    public FeedContextDto getFeedContext(Long userId) {
        User user = getUserById(userId);

        List<Long> followingIds = user.getFollowing().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<Integer> tagIds = user.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        List<Long> groupIds = List.of();

        return new FeedContextDto(userId, followingIds, tagIds, groupIds);
    }

    @Transactional(readOnly = true)
    public Page<UserCardDto> searchUsers(Long currentUserId, String query, Pageable pageable) {
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> myDois = me.getPublications().stream()
                .map(pa -> pa.getPublication().getDoi())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<User> candidates = userRepository.findUsersForSearch(currentUserId,
                (query != null && !query.isEmpty()) ? query.toLowerCase() : null);

        List<UserCardDto> results = candidates.stream()
                .map(candidate -> {
                    double score = 0;

                    long commonArticles = candidate.getPublications().stream()
                            .map(pa -> pa.getPublication().getDoi())
                            .filter(doi -> doi != null && myDois.contains(doi))
                            .count();
                    score += commonArticles * 100;

                    boolean sameEdu = candidate.getEducation().stream()
                            .anyMatch(ce -> me.getEducation().stream()
                                    .anyMatch(meEdu -> meEdu.getOrganization().getId().equals(ce.getOrganization().getId())));
                    if (sameEdu) score += 30;

                    Set<String> myTagNames = me.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
                    Set<String> commonTags = candidate.getTags().stream()
                            .map(Tag::getName)
                            .filter(myTagNames::contains)
                            .collect(Collectors.toSet());
                    score += commonTags.size() * 10;

                    UserCardDto dto = new UserCardDto();
                    dto.setId(candidate.getId());
                    dto.setUsername(candidate.getUsername());
                    dto.setFirstName(candidate.getFirstName());
                    dto.setLastName(candidate.getLastName());
                    dto.setRecommendationScore(score);
                    dto.setCommonTags(commonTags);

                    if (candidate.getCity() != null) dto.setCityName(candidate.getCity().getName());

                    candidate.getEducation().stream().findFirst().ifPresent(edu -> {
                        dto.setOrganizationName(edu.getOrganization().getName());
                    });

                    return dto;
                })
                .sorted(Comparator.comparing(UserCardDto::getRecommendationScore).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());

        if (start > results.size()) return new PageImpl<>(new ArrayList<>(), pageable, results.size());
        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }
}
