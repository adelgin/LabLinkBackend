package org.example.lablinkbackend.domain.publication.controller;

import lombok.AllArgsConstructor;
import org.example.lablinkbackend.domain.publication.model.dto.Publication;
import org.example.lablinkbackend.domain.publication.service.PublicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping(path = "api/publications")
public class PublicationsController {

    private PublicationService publicationService;

    @GetMapping
    public Publication getPublications(@PathVariable Long id) {
        return publicationService.getPublication(id);
    }
}
