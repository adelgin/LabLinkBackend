package org.example.lablinkbackend.domain.social.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "friendships")
@Data
public class Friendship {
    public Friendship(FriendshipId id, FriendshipStatus status) {
        this.id = id;
        this.status = status;
    }

    @EmbeddedId
    private FriendshipId id;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Embeddable @Data @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendshipId implements Serializable {
        private Long userId;
        private Long friendId;
    }
}