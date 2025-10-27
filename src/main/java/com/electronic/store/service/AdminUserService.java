package com.electronic.store.service;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.User;
import com.electronic.store.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy danh sách users với filter, search và pagination
     */
    public Page<User> getUsers(User.Role role, User.Status status, String search,
                              int page, int size, String sortBy, String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            return userRepository.findUsersWithFilters(role, status, search, pageable);
        } catch (Exception e) {
            logger.error("Error getting users list", e);
            throw new RuntimeException("Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }
    }

    /**
     * Lấy chi tiết user theo ID
     */
    public ApiResponse<User> getUserById(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Người dùng không tồn tại");
            }

            User user = userOpt.get();
            // Tạo copy để không expose password
            User userResponse = createUserResponse(user);

            return ApiResponse.success("Lấy thông tin người dùng thành công", userResponse);
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", userId, e);
            return ApiResponse.error("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
    }

    /**
     * Cập nhật trạng thái user (ACTIVE/BLOCKED)
     */
    public ApiResponse<String> updateUserStatus(Long userId, User.Status newStatus) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Người dùng không tồn tại");
            }

            User user = userOpt.get();

            // Không cho phép block admin
            if (user.getRole() == User.Role.ADMIN && newStatus == User.Status.BLOCKED) {
                return ApiResponse.error("Không thể khóa tài khoản admin");
            }

            user.setStatus(newStatus);
            userRepository.save(user);

            String action = newStatus == User.Status.ACTIVE ? "kích hoạt" : "khóa";
            logger.info("User status updated: {} - {}", userId, newStatus);

            return ApiResponse.success("Đã " + action + " tài khoản thành công");
        } catch (Exception e) {
            logger.error("Error updating user status: {}", userId, e);
            return ApiResponse.error("Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    /**
     * Xóa user (soft delete - chuyển status thành BLOCKED)
     */
    public ApiResponse<String> deleteUser(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Người dùng không tồn tại");
            }

            User user = userOpt.get();

            // Không cho phép xóa admin
            if (user.getRole() == User.Role.ADMIN) {
                return ApiResponse.error("Không thể xóa tài khoản admin");
            }

            // Soft delete: chuyển status thành BLOCKED
            user.setStatus(User.Status.BLOCKED);
            userRepository.save(user);

            logger.info("User soft deleted: {}", userId);

            return ApiResponse.success("Đã xóa người dùng thành công");
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            return ApiResponse.error("Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }

    /**
     * Xóa nhiều users cùng lúc
     */
    public ApiResponse<String> deleteMultipleUsers(List<Long> userIds) {
        try {
            List<User> users = userRepository.findByIdIn(userIds);

            if (users.isEmpty()) {
                return ApiResponse.error("Không tìm thấy người dùng nào");
            }

            int deletedCount = 0;
            int adminCount = 0;

            for (User user : users) {
                if (user.getRole() == User.Role.ADMIN) {
                    adminCount++;
                    continue;
                }

                user.setStatus(User.Status.BLOCKED);
                deletedCount++;
            }

            if (deletedCount > 0) {
                userRepository.saveAll(users);
            }

            String message = String.format("Đã xóa %d người dùng", deletedCount);
            if (adminCount > 0) {
                message += String.format(" (%d admin không thể xóa)", adminCount);
            }

            logger.info("Bulk delete completed: {} users deleted", deletedCount);

            return ApiResponse.success(message);
        } catch (Exception e) {
            logger.error("Error bulk deleting users", e);
            return ApiResponse.error("Lỗi khi xóa nhiều người dùng: " + e.getMessage());
        }
    }

    /**
     * Lấy thống kê users
     */
    public ApiResponse<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Tổng số users
            long totalUsers = userRepository.getTotalUsersCount();
            stats.put("totalUsers", totalUsers);

            // Số users theo role
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            long userCount = userRepository.countByRole(User.Role.USER);
            stats.put("adminCount", adminCount);
            stats.put("userCount", userCount);

            // Số users theo status
            long activeCount = userRepository.countByStatus(User.Status.ACTIVE);
            long blockedCount = userRepository.countByStatus(User.Status.BLOCKED);
            stats.put("activeCount", activeCount);
            stats.put("blockedCount", blockedCount);

            // Thống kê đăng ký theo tháng (năm hiện tại)
            int currentYear = LocalDateTime.now().getYear();
            List<Object[]> monthlyStats = userRepository.getUserRegistrationStatsByYear(currentYear);
            stats.put("monthlyRegistrations", monthlyStats);

            return ApiResponse.success("Lấy thống kê thành công", stats);
        } catch (Exception e) {
            logger.error("Error getting user stats", e);
            return ApiResponse.error("Lỗi khi lấy thống kê: " + e.getMessage());
        }
    }

    /**
     * Tìm users theo từ khóa
     */
    public Page<User> searchUsers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            return userRepository.findUsersWithFilters(null, null, keyword, pageable);
        } catch (Exception e) {
            logger.error("Error searching users with keyword: {}", keyword, e);
            throw new RuntimeException("Lỗi khi tìm kiếm người dùng: " + e.getMessage());
        }
    }

    /**
     * Lấy top users có nhiều orders nhất
     */
    public Page<User> getTopUsersByOrders(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return userRepository.findTopUsersByOrderCount(pageable);
        } catch (Exception e) {
            logger.error("Error getting top users by orders", e);
            throw new RuntimeException("Lỗi khi lấy top users: " + e.getMessage());
        }
    }

    /**
     * Helper method để tạo User response không có password
     */
    private User createUserResponse(User user) {
        User userResponse = new User();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setPhone(user.getPhone());
        userResponse.setAddress(user.getAddress());
        userResponse.setRole(user.getRole());
        userResponse.setStatus(user.getStatus());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        // Không set password để giữ null
        return userResponse;
    }
}