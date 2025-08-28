package com.github.datlaipro.shop.domain.user.service;

import com.github.datlaipro.shop.domain.user.dto.RegisterReq;
import com.github.datlaipro.shop.domain.user.dto.UserRes;
import com.github.datlaipro.shop.domain.user.entity.UserEntity;
import com.github.datlaipro.shop.domain.user.exception.EmailAlreadyUsedException;
import com.github.datlaipro.shop.domain.user.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserRes register(RegisterReq req) {
    final String email = (req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase());
    if (email.isEmpty()) {
      throw new IllegalArgumentException("Email không được để trống");
    }
    if (userRepo.existsByEmailIgnoreCase(email)) {
      throw new EmailAlreadyUsedException(email);
    }

    String name    = StringUtils.hasText(req.getName()) ? req.getName().trim() : null;
    String avatar  = StringUtils.hasText(req.getAvatar()) ? req.getAvatar().trim() : null;
    String address = StringUtils.hasText(req.getAddress()) ? req.getAddress().trim() : null;

    UserEntity user = new UserEntity();
    user.setName(name);
    user.setEmail(email); // 👈 đã chuẩn hoá
    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    user.setAvatar(avatar);
    user.setAddress(address);
    user.setRole("user");

    try {
      user = userRepo.save(user);
    } catch (org.springframework.dao.DataIntegrityViolationException ex) {
      // Phòng trường hợp 2 request song song cùng email
      throw new EmailAlreadyUsedException(email);
    }

    return new UserRes(
      user.getId(), user.getName(), user.getEmail(),
      user.getAvatar(), user.getAddress(), user.getRole()
    );
  }
}
