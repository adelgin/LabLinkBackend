package org.example.lablinkbackend.domain.publication.service;

import jakarta.transaction.Transactional;
import jdk.jfr.TransitionTo;
import lombok.AllArgsConstructor;
import org.example.lablinkbackend.domain.publication.model.dto.Publication;
import org.example.lablinkbackend.domain.publication.repository.PublicationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PublicationService {
    private PublicationsRepository publicationsRepository;

    @Transactional
    public boolean save(List<Publication> publications) {
        try {
            publicationsRepository.saveAll(publications);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Publication getPublication(Long id) {
        return publicationsRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Publication not found")
        );
    }
}
