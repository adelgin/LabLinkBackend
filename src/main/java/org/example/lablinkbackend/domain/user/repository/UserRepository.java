package org.example.lablinkbackend.domain.user.repository;

import org.example.lablinkbackend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);

    // Entity graph
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tags " +
            "LEFT JOIN FETCH u.city c " +
            "LEFT JOIN FETCH c.country " +
            "LEFT JOIN FETCH u.education edu " +
            "LEFT JOIN FETCH edu.organization " +
            "LEFT JOIN FETCH u.career car " +
            "LEFT JOIN FETCH car.organization " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithProfile(String username);

    @Query(value = "SELECT DISTINCT u.* FROM users u " +
            "LEFT JOIN cities c ON c.id = u.city_id " +
            "WHERE u.is_deleted = false AND u.id != :currentUserId " +
            "AND (:query IS NULL OR :query = '' OR " +
            "u.first_name::text ILIKE '%' || :query || '%' OR " +
            "u.last_name::text ILIKE '%' || :query || '%' OR " +
            "u.username::text ILIKE '%' || :query || '%')",
            nativeQuery = true)
    List<User> findUsersForSearch(@Param("currentUserId") Long currentUserId, @Param("query") String query);
}
