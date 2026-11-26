package com.studyenglish.backend.repos;

import com.studyenglish.backend.models.api.User;
import com.studyenglish.backend.models.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
            select u from User u
            where lower(u.name) like lower(:pattern) escape '!'
               or lower(u.nickname) like lower(:pattern) escape '!'
            """)
    Page<User> search(@Param("pattern") String pattern, Pageable pageable);

    @Query("""
            select u from User u
            where u.role = :role
              and (lower(u.name) like lower(:pattern) escape '!'
                   or lower(u.nickname) like lower(:pattern) escape '!')
            """)
    Page<User> searchByRole(@Param("role") UserRole role, @Param("pattern") String pattern, Pageable pageable);
}
