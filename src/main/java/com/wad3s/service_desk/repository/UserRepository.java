package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = { "roles", "location" })
    Optional<User> findByEmail(String email);

    @Query("""
           select u from User u 
           left join fetch u.roles 
           where u.id = :id
           """)
    Optional<User> findByIdWithRoles(Long id);

    boolean existsByEmail(String email);
}