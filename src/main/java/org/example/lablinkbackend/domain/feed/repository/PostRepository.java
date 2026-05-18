package org.example.lablinkbackend.domain.feed.repository;

import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Integer countByParentPostId(Long id);
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
    Page<Post> findAllByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.author a " +
            "LEFT JOIN a.tags t " +
            "WHERE p.createdAt > :threshold " +
            "AND (p.author.id IN :friendIds " +
            "OR p.group.id IN :groupIds " +
            "OR t.id IN :userTagIds)")
    List<Post> findFeedCandidates(
            @Param("friendIds") List<Long> friendIds,
            @Param("groupIds") List<Long> groupIds,
            @Param("userTagIds") List<Integer> userTagIds,
            @Param("threshold") LocalDateTime threshold
    );
}
