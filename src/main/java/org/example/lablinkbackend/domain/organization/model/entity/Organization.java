package org.example.lablinkbackend.domain.organization.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.lablinkbackend.domain.geo.model.entity.City;
import org.example.lablinkbackend.domain.organization.model.enums.OrganizationType;

@Entity
@Table(name = "organizations")
@Data
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_type", nullable = false)
    private OrganizationType orgType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    private String website;
}
