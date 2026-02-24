package org.example.lablinkbackend.domain.organization.service;

import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.example.lablinkbackend.domain.organization.model.enums.OrganizationType;
import org.example.lablinkbackend.domain.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<Organization> getEducationalOrganizations() {
        return organizationRepository.findAllByOrgTypeIn(List.of(
                OrganizationType.UNIVERSITY,
                OrganizationType.SCHOOL,
                OrganizationType.RESEARCH_INSTITUTE
        ));
    }

    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }
}
