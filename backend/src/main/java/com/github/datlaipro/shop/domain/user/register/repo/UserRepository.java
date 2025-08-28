package com.github.datlaipro.shop.domain.user.repo;

import com.github.datlaipro.shop.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  boolean existsByEmail(String email);

  // 👉 dùng mấy method dưới thay cho 2 cái trên ở flow auth
  boolean existsByEmailIgnoreCase(String email);
  Optional<UserEntity> findByEmailIgnoreCase(String email);
}
