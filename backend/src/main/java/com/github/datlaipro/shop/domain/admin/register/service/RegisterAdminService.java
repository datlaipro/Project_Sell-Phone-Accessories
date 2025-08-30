package com.github.datlaipro.shop.domain.admin.register.service;

import com.github.datlaipro.shop.domain.admin.register.dto.RegisterReqAdmin;
import com.github.datlaipro.shop.domain.admin.register.dto.RegisterResAdmin;
import com.github.datlaipro.shop.domain.admin.register.entity.AdminEntity;
import com.github.datlaipro.shop.domain.user.exception.EmailAlreadyUsedException;
import com.github.datlaipro.shop.domain.admin.register.repo.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RegisterAdminService {
  private final AdminRepository adminRepo;
  private final PasswordEncoder passwordEncoder;

  public RegisterAdminService(AdminRepository adminRepo, PasswordEncoder passwordEncoder) {
    this.adminRepo = adminRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public RegisterResAdmin register(RegisterReqAdmin req) {
    final String email = (req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase());
    if (email.isEmpty()) {
      throw new IllegalArgumentException("Email không được để trống");
    }
    if (adminRepo.existsByEmailIgnoreCase(email)) {
      throw new EmailAlreadyUsedException(email);
    }

    String name = StringUtils.hasText(req.getName()) ? req.getName().trim() : null;
    String avatar = StringUtils.hasText(req.getAvatar()) ? req.getAvatar().trim() : null;

    AdminEntity admin = new AdminEntity();
    admin.setName(name);
    admin.setEmail(email); // 👈 đã chuẩn hoá
    admin.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    admin.setAvatar(avatar);
    admin.setRole(AdminEntity.Role.superadmin);

    try {
      admin = adminRepo.save(admin);
    } catch (org.springframework.dao.DataIntegrityViolationException ex) {
      // Phòng trường hợp 2 request song song cùng email
      throw new EmailAlreadyUsedException(email);
    }

    return new RegisterResAdmin(
        admin.getId(), admin.getName(), admin.getEmail(),
        admin.getAvatar(), // 👈 thêm address
        admin.getRole().name());
  }
}
