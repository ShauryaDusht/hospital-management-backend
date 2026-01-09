package com.shaurya.hospitalManagement.repository;

import com.shaurya.hospitalManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    // To find user with a username
    Optional<User> findByUsername(String username);
}