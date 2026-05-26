package org.example.lablinkbackend.domain.user.repository;

import org.example.lablinkbackend.domain.user.model.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscription.UserSubscriptionKey> {
    List<UserSubscription> findById_FollowerId(Long followerId);
    boolean existsById_FollowerIdAndId_TargetId(Long followerId, Long targetId);
}
