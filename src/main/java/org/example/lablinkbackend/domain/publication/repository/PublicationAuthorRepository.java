package org.example.lablinkbackend.domain.publication.repository;

import org.example.lablinkbackend.domain.publication.model.dto.PublicationAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationAuthorRepository extends JpaRepository<PublicationAuthor, PublicationAuthor.PublicationAuthorId> {
}
