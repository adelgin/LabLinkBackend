package org.example.lablinkbackend.domain.user.repository;

import org.example.lablinkbackend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
