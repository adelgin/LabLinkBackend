package org.example.lablinkbackend.domain.social.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.domain.social.model.dto.FriendRequestDto;
import org.example.lablinkbackend.domain.social.model.entity.Friendship;
import org.example.lablinkbackend.domain.social.model.entity.FriendshipStatus;
import org.example.lablinkbackend.domain.social.repository.FriendshipRepository;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.model.entity.UserSubscription;
import org.example.lablinkbackend.domain.user.repository.UserSubscriptionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public void sendFriendRequest(Long userId, Long friendId) {
        if (userId.equals(friendId)) throw new RuntimeException("You can not add yourself to friends!");

        Friendship.FriendshipId id = new Friendship.FriendshipId(userId, friendId);

        if (friendshipRepository.existsById(id)) {
            throw new RuntimeException("You also sent the friend request!");
        }

        Friendship friendship = new Friendship(id, FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);

        // Формируем подписку при подаче принятии заявки в друзья
        createSubscriptionIfNotExists(userId, friendId);
    }

    public void acceptFriendRequest(Long userId, Long friendId) {
        Friendship request = friendshipRepository.findByIdUserIdAndIdFriendId(friendId, userId).orElseThrow(() -> new RuntimeException("No friend accept request found!"));

        request.setStatus(FriendshipStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendshipRepository.save(request);

        Friendship reverseFriendship = new Friendship();
        reverseFriendship.setId(new Friendship.FriendshipId(userId, friendId));
        reverseFriendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(reverseFriendship);
    }

    private void createSubscriptionIfNotExists(Long followerId, Long targetId) {
        if (followerId.equals(targetId)) {
            return;
        }
        UserSubscription.UserSubscriptionKey key =
                new UserSubscription.UserSubscriptionKey(followerId, targetId);

        if (!userSubscriptionRepository.existsById(key)) {
            UserSubscription subscription = new UserSubscription();
            subscription.setId(key);

            try {
                userSubscriptionRepository.save(subscription);
                System.out.println("Subscription created: " + followerId + " -> " + targetId);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Subscription already exists, skipping");
            }
        }
    }

    public void removeFriendshipRequest(Long userId, Long notFriendId) {
        if (userId.equals(notFriendId)) {
            throw new RuntimeException("You cannot interact with yourself!");
        }

        Friendship.FriendshipId directId = new Friendship.FriendshipId(userId, notFriendId);
        Friendship.FriendshipId reverseId = new Friendship.FriendshipId(notFriendId, userId);

        Friendship existingFriendship = friendshipRepository.findById(directId)
                .orElse(null);

        if (existingFriendship == null) {
            existingFriendship = friendshipRepository.findById(reverseId)
                    .orElseThrow(() -> new RuntimeException("No friendship or friend request found!"));
        }

        friendshipRepository.delete(existingFriendship);

        if (existingFriendship.getStatus() == FriendshipStatus.ACCEPTED) {
            friendshipRepository.findById(reverseId)
                    .ifPresent(friendshipRepository::delete);
        }

        // Сначала удаляем связи у пользователя, затем у недруга xD
        removeSubscriptionIfExists(userId, notFriendId);
        removeSubscriptionIfExists(notFriendId, userId);
    }

    private void removeSubscriptionIfExists(Long followerId, Long targetId) {
        UserSubscription.UserSubscriptionKey key =
                new UserSubscription.UserSubscriptionKey(followerId, targetId);

        if (userSubscriptionRepository.existsById(key)) {
            try {
                userSubscriptionRepository.deleteById(key);
                System.out.println("Subscription removed: " + followerId + " -> " + targetId);
            } catch (Exception e) {
                System.out.println("Failed to remove subscription: " + e.getMessage());
            }
        }
    }

    public List<FriendRequestDto> getIncomingRequests(Long currentUserId) {
        return friendshipRepository.findIncomingRequests(currentUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FriendRequestDto convertToDto(User user) {
        return new FriendRequestDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getCity() != null ? user.getCity().getName() : null
        );
    }
}
