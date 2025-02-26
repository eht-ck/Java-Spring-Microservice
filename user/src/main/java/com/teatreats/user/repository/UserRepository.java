package com.teatreats.user.repository;

import com.teatreats.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
  User findByUserName(String username);

  boolean existsByUserName(String userName);

  boolean existsByEmail(String email);
}
