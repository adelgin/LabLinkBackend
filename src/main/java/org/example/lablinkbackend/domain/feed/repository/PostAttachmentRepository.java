package org.example.lablinkbackend.domain.feed.repository;

import org.example.lablinkbackend.domain.feed.model.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {
}
