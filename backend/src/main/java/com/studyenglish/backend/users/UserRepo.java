package com.studyenglish.backend.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    @Query("select u from User u where lower(u.email) = lower(:email)")
    Optional<User> findByEmail(@Param("email") String email);
    @Query("select case when count(u) > 0 then true else false end " +
            "from User u where lower(u.email) = lower(:email)")
    boolean existsByEmail(@Param("email") String email);
    @Query("select case when count(u) > 0 then true else false end " +
            "from User u where lower(u.nickname) = lower(:nickname)")
    boolean existsByNickname(@Param("nickname") String nickname);
    boolean existsByRole(UserRole role);
}
