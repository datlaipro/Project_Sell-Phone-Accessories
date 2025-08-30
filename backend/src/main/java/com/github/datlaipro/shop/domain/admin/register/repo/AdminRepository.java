package com.github.datlaipro.shop.domain.admin.register.repo;


import com.github.datlaipro.shop.domain.admin.register.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
  boolean existsByEmail(String email);// truy vấn db nếu trùng email thì báo lỗi 

  // 👉 dùng mấy method dưới thay cho 2 cái trên ở flow auth
  boolean existsByEmailIgnoreCase(String email);// truy vấn db nếu trùng email thì báo lỗi và không phân biệt viết hoa thường 
  Optional<AdminEntity> findByEmailIgnoreCase(String email);// trả về user có email đó (nếu tồn tại)
}
