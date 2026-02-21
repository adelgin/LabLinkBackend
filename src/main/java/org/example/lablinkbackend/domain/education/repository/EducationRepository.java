package org.example.lablinkbackend.domain.education.repository;

import org.example.lablinkbackend.domain.education.model.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
}
