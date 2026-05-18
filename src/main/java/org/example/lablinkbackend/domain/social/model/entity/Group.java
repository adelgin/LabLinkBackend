package org.example.lablinkbackend.domain.social.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.util.List;

@Entity
@Table(name = "groups")
@Data
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "group")
    private List<Post> posts;
}
