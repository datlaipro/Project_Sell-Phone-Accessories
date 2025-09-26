package com.github.datlaipro.shop.domain.admin.login.repo;

import com.github.datlaipro.shop.domain.admin.login.entity.AdminRefreshTokenEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshTokenEntity, Long> {

    // ===== Truy vấn theo jti =====
    Optional<AdminRefreshTokenEntity> findByJti(String jti);
    Optional<AdminRefreshTokenEntity> findByJtiAndRevokedAtIsNull(String jti);

    // KHÓA bản ghi để tránh 2 refresh song song dùng cùng 1 token
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from AdminRefreshTokenEntity t where t.jti = :jti")
    Optional<AdminRefreshTokenEntity> findByJtiForUpdate(@Param("jti") String jti);

    // ===== Theo familyId =====
    // Lấy tất cả token "còn sống" (chưa revoke) trong cùng family
    @Query("select t from AdminRefreshTokenEntity t where t.familyId = :fid and t.revokedAt is null")
    List<AdminRefreshTokenEntity> findAliveByFamily(@Param("fid") String familyId);

    // Revoke cả family (UPDATE một phát)
    @Modifying
    @Query("update AdminRefreshTokenEntity t set t.revokedAt = :now where t.familyId = :fid and t.revokedAt is null")
    int revokeFamily(@Param("fid") String familyId, @Param("now") LocalDateTime now);
}
