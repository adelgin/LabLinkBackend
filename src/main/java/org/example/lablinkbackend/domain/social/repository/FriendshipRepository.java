package org.example.lablinkbackend.domain.social.repository;

import org.example.lablinkbackend.domain.social.model.entity.Friendship;
import org.example.lablinkbackend.domain.social.model.entity.FriendshipStatus;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
    List<Friendship> findAllByIdUserId(Long userId);
    Optional<Friendship> findByIdUserIdAndIdFriendId(Long userId, Long friendId);
    List<Friendship> findAllByIdFriendIdAndStatus(Long friendId, FriendshipStatus status);

    @Query("SELECT u FROM User u JOIN Friendship f ON u.id = f.id.userId " +
            "WHERE f.id.friendId = :currentUserId AND f.status = 'PENDING'")
    List<User> findIncomingRequests(@Param("currentUserId") Long currentUserId);

    @Query("SELECT u FROM User u JOIN Friendship f ON (u.id = f.id.userId OR u.id = f.id.friendId) " +
            "WHERE (f.id.userId = :currentUserId OR f.id.friendId = :currentUserId) " +
            "AND u.id != :currentUserId AND f.status = 'ACCEPTED'")
    List<User> findAcceptedFriends(@Param("currentUserId") Long currentUserId);
}
