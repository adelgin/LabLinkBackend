package org.example.lablinkbackend.domain.career.model.repository;

import org.example.lablinkbackend.domain.career.model.entity.CareerExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<CareerExperience, Long> {
}
