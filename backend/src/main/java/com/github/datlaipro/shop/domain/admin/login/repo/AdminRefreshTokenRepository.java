package com.github.datlaipro.shop.domain.admin.login.repo;

import com.github.datlaipro.shop.domain.admin.login.entity.AdminRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AdminRefreshTokenRepository
        extends JpaRepository<AdminRefreshTokenEntity, Long> {

    Optional<AdminRefreshTokenEntity> findByJti(String jti);
    Optional<AdminRefreshTokenEntity> findByJtiAndRevokedAtIsNull(String jti);

    // dùng cho revoke cả family (thay vì findAll().stream().filter(...))
    List<AdminRefreshTokenEntity> findByFamilyIdAndRevokedAtIsNull(String familyId);
}
