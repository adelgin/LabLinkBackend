package org.example.lablinkbackend.domain.organization.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.example.lablinkbackend.domain.organization.service.OrganizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organization")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Operation(
            summary = "Получить все образовательные организации",
            description = "Возвращает список всех образовательных организаций"
    )
    @GetMapping("/all_educational")
    public List<Organization> getAllEducationalOrganizations() {
        return this.organizationService.getEducationalOrganizations();
    }

    @Operation(
            summary = "Получить все организации",
            description = "Возвращает список всех организаций"
    )
    @GetMapping("/all")
    public List<Organization> getAll() {
        return this.organizationService.getAll();
    }
}
