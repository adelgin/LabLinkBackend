package org.example.lablinkbackend.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.lablinkbackend.domain.user.model.dto.user.UserSubscriptionDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
@Data
public class UserSubscription {
    @EmbeddedId
    UserSubscriptionKey id;

    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    public UserSubscriptionDto convertToDto() {
        return new UserSubscriptionDto(id.getFollowerId(), id.getTargetId(), this.getCreatedAt());
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSubscriptionKey implements Serializable {
        @Column(name = "follower_id", nullable = false)
        private Long followerId;

        @Column(name = "target_id", nullable = false)
        private Long targetId;
    }
}
