package com.github.datlaipro.shop.domain.user.repo;

import com.github.datlaipro.shop.domain.user.entity.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

// thực hiện truy vấn db qua jpa
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
  Optional<UserRefreshTokenEntity> findByJti(String jti);// tìm 

  Optional<UserRefreshTokenEntity> findByJtiAndRevokedAtIsNull(String jti);

  int countByFamilyIdAndRevokedAtIsNullAndExpiresAtAfter(String familyId, LocalDateTime now);
}
