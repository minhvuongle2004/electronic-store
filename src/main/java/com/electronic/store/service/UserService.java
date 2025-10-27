package com.electronic.store.service;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.User;
import com.electronic.store.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lấy thông tin người dùng theo ID
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Lấy thông tin người dùng theo username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Cập nhật thông tin cá nhân người dùng
     */
    public ApiResponse<User> updateProfile(Long userId, String fullName, String email, String phone, String address) {
        try {
            // 1. Tìm user theo ID
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Người dùng không tồn tại");
            }

            User user = userOpt.get();

            // 2. Kiểm tra email có bị trùng với user khác không
            if (email != null && !email.equals(user.getEmail())) {
                if (userRepository.existsByEmailAndIdNot(email, userId)) {
                    return ApiResponse.error("Email đã được sử dụng bởi người dùng khác");
                }
            }

            // 3. Cập nhật thông tin
            if (fullName != null && !fullName.trim().isEmpty()) {
                user.setFullName(fullName.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email.trim());
            }
            if (phone != null) {
                user.setPhone(phone.trim().isEmpty() ? null : phone.trim());
            }
            if (address != null) {
                user.setAddress(address.trim().isEmpty() ? null : address.trim());
            }

            // 4. Lưu thay đổi
            User updatedUser = userRepository.save(user);

            // 5. Tạo copy để trả về (không modify object gốc đã save)
            User userResponse = new User();
            userResponse.setId(updatedUser.getId());
            userResponse.setUsername(updatedUser.getUsername());
            userResponse.setEmail(updatedUser.getEmail());
            userResponse.setFullName(updatedUser.getFullName());
            userResponse.setPhone(updatedUser.getPhone());
            userResponse.setAddress(updatedUser.getAddress());
            userResponse.setRole(updatedUser.getRole());
            userResponse.setStatus(updatedUser.getStatus());
            userResponse.setCreatedAt(updatedUser.getCreatedAt());
            // Không set password để giữ null

            logger.info("User profile updated successfully for user ID: {}", userId);

            return ApiResponse.success("Cập nhật thông tin thành công", userResponse);

        } catch (Exception e) {
            logger.error("Error updating profile for user ID: {}", userId, e);
            return ApiResponse.error("Lỗi khi cập nhật thông tin: " + e.getMessage());
        }
    }

    /**
     * Đổi mật khẩu
     */
    public ApiResponse<String> changePassword(Long userId, String currentPassword, String newPassword) {
        try {
            // 1. Validate input
            if (newPassword == null || newPassword.length() < 6) {
                return ApiResponse.error("Mật khẩu mới phải có ít nhất 6 ký tự");
            }

            // 2. Tìm user theo ID
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Người dùng không tồn tại");
            }

            User user = userOpt.get();

            // 3. Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ApiResponse.error("Mật khẩu hiện tại không đúng");
            }

            // 4. Mã hóa và lưu mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            logger.info("Password changed successfully for user ID: {}", userId);

            return ApiResponse.success("Đổi mật khẩu thành công");

        } catch (Exception e) {
            logger.error("Error changing password for user ID: {}", userId, e);
            return ApiResponse.error("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra email có khả dụng cho user hiện tại không
     */
    public boolean isEmailAvailableForUser(String email, Long userId) {
        return !userRepository.existsByEmailAndIdNot(email, userId);
    }
}