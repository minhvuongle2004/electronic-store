package com.electronic.store.repository;

import com.electronic.store.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo username (dùng cho đăng nhập)
    Optional<User> findByUsername(String username);

    // Tìm user theo email
    Optional<User> findByEmail(String email);

    // Tìm user theo verification token
    User findByVerificationToken(String token);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm user theo username và status ACTIVE
    Optional<User> findByUsernameAndStatus(String username, User.Status status);

    // Tìm user theo username hoặc email (dùng cho đăng nhập)
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Kiểm tra email đã tồn tại chưa (trừ user hiện tại)
    boolean existsByEmailAndIdNot(String email, Long id);

    // ===== ADMIN USER MANAGEMENT METHODS =====

    /**
     * Lọc users theo nhiều tiêu chí với pagination
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:search IS NULL OR u.username LIKE %:search% OR u.email LIKE %:search% OR u.fullName LIKE %:search% OR u.phone LIKE %:search%) " +
           "ORDER BY u.createdAt DESC")
    Page<User> findUsersWithFilters(@Param("role") User.Role role,
                                    @Param("status") User.Status status,
                                    @Param("search") String search,
                                    Pageable pageable);

    /**
     * Đếm users theo role
     */
    long countByRole(User.Role role);

    /**
     * Đếm users theo status
     */
    long countByStatus(User.Status status);

    /**
     * Tìm users theo role với pagination
     */
    Page<User> findByRoleOrderByCreatedAtDesc(User.Role role, Pageable pageable);

    /**
     * Tìm users theo status với pagination
     */
    Page<User> findByStatusOrderByCreatedAtDesc(User.Status status, Pageable pageable);

    /**
     * Tìm users theo fullName chứa keyword
     */
    Page<User> findByFullNameContainingIgnoreCaseOrderByCreatedAtDesc(String fullName, Pageable pageable);

    /**
     * Tìm users được tạo trong khoảng thời gian
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate ORDER BY u.createdAt DESC")
    Page<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate,
                                      Pageable pageable);

    /**
     * Soft delete user (set status to BLOCKED thay vì xóa hẳn)
     */
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    @org.springframework.data.jpa.repository.Modifying
    int updateUserStatus(@Param("userId") Long userId, @Param("status") User.Status status);

    /**
     * Tìm users với multiple IDs
     */
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByIdIn(@Param("userIds") List<Long> userIds);

    /**
     * Thống kê tổng số users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long getTotalUsersCount();

    /**
     * Thống kê users đăng ký theo tháng
     */
    @Query("SELECT MONTH(u.createdAt) as month, YEAR(u.createdAt) as year, COUNT(u) as count " +
           "FROM User u WHERE YEAR(u.createdAt) = :year " +
           "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
           "ORDER BY MONTH(u.createdAt)")
    List<Object[]> getUserRegistrationStatsByYear(@Param("year") int year);

    /**
     * Tìm top users có nhiều orders nhất
     */
    @Query("SELECT u FROM User u LEFT JOIN u.orders o " +
           "GROUP BY u.id ORDER BY COUNT(o) DESC")
    Page<User> findTopUsersByOrderCount(Pageable pageable);
}