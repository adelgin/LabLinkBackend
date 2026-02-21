package org.example.lablinkbackend.domain.organization.repository;

import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
