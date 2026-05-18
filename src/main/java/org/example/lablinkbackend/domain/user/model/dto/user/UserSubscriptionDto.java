package org.example.lablinkbackend.domain.user.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserSubscriptionDto {
    private Long followerId;
    private Long targetId;

    private LocalDateTime createdAt;
}
