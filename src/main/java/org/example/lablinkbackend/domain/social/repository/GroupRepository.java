package org.example.lablinkbackend.domain.social.repository;

import org.example.lablinkbackend.domain.social.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
