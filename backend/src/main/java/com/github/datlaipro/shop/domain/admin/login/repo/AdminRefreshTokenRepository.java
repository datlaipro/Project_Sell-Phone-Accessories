package com.github.datlaipro.shop.domain.admin.login.repo;

import com.github.datlaipro.shop.domain.admin.login.entity.AdminRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

// thực hiện truy vấn db qua jpa
public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshTokenRepository, Long> {
  Optional<AdminRefreshTokenRepository> findByJti(String jti);// tìm 

  Optional<AdminRefreshTokenRepository> findByJtiAndRevokedAtIsNull(String jti);

  int countByFamilyIdAndRevokedAtIsNullAndExpiresAtAfter(String familyId, LocalDateTime now);
}
