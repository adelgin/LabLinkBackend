package org.example.lablinkbackend.domain.career.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.time.LocalDate;

@Entity
@Table(name = "career_experience")
@Data
public class CareerExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    private String title;
    private String department;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;

    @Column(columnDefinition = "TEXT")
    private String description;
}