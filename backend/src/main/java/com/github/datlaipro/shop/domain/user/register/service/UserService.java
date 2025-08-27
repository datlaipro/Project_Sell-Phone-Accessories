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
    public UserRes register(RegisterReq req) {//kiểm tra nếu trùng email đăng kí thì ném lỗi 
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyUsedException(req.getEmail());
        }

        // Chuẩn hoá: empty string -> null
        String name    = StringUtils.hasText(req.getName()) ? req.getName().trim() : null;
        String avatar  = StringUtils.hasText(req.getAvatar()) ? req.getAvatar().trim() : null;
        String address = StringUtils.hasText(req.getAddress()) ? req.getAddress().trim() : null;

        // KHÔNG dùng builder() vì UserEntity không có Lombok @Builder
        UserEntity user = new UserEntity();// thực hiện truy vấn vào db 
        user.setName(name);
        user.setEmail(req.getEmail().trim());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setAvatar(avatar);      // có thể null
        user.setAddress(address);    // có thể null
        user.setRole("user");        // khớp enum DB ('user','admin_proxy')

        user = userRepo.save(user);

        return new UserRes(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatar(),
                user.getAddress(),
                user.getRole()
        );
    }
}
