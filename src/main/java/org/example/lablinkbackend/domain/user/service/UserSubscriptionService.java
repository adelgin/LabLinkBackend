package org.example.lablinkbackend.domain.user.service;

import lombok.AllArgsConstructor;
import org.example.lablinkbackend.domain.user.model.dto.user.UserSubscriptionDto;
import org.example.lablinkbackend.domain.user.model.entity.UserSubscription;
import org.example.lablinkbackend.domain.user.repository.UserSubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionDto makeSubscription(Long followerId, Long targetId) {
        if (followerId.equals(targetId)) throw new RuntimeException("You can not add subscription for yourself!");

        UserSubscription.UserSubscriptionKey id = new UserSubscription.UserSubscriptionKey();
        id.setFollowerId(followerId);
        id.setTargetId(targetId);

        if (userSubscriptionRepository.existsById(id)) {
            throw new RuntimeException("You also have subscription for this user!");
        }

        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setId(id);
        userSubscriptionRepository.save(userSubscription);

        return userSubscription.convertToDto();
    }
}
