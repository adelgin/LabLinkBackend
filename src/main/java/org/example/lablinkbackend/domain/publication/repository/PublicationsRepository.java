package org.example.lablinkbackend.domain.publication.repository;

import org.example.lablinkbackend.domain.publication.model.dto.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationsRepository extends JpaRepository<Publication, Long> {
    boolean existsByDoi(String doi);
}
