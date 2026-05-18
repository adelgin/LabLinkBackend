package org.example.lablinkbackend.domain.feed.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();
}