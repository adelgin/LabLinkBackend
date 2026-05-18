package org.example.lablinkbackend.domain.user.repository;

import org.example.lablinkbackend.domain.user.model.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscription.UserSubscriptionKey> {
}
