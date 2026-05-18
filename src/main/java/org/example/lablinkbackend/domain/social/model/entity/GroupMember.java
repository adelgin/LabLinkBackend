package org.example.lablinkbackend.domain.social.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Data
public class GroupMember {

    @EmbeddedId
    private GroupMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "role")
    private String role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}

