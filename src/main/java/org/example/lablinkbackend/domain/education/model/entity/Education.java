package org.example.lablinkbackend.domain.education.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.lablinkbackend.domain.education.model.enums.EducationLevel;
import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.time.LocalDate;

@Entity
@Table(name = "user_education")
@Data
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    private EducationLevel degree;

    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
}
