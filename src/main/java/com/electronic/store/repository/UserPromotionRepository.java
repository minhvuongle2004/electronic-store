package com.electronic.store.repository;

import com.electronic.store.entity.UserPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPromotionRepository extends JpaRepository<UserPromotion, Long> {

    /**
     * Kiểm tra người dùng đã sử dụng khuyến mãi chưa
     */
    boolean existsByUserIdAndPromotionId(Long userId, Long promotionId);

    /**
     * Kiểm tra user đã sử dụng promotion code này chưa
     */
    @Query("SELECT COUNT(up) > 0 FROM UserPromotion up " +
           "WHERE up.user.id = :userId AND up.promotion.code = :promotionCode")
    boolean existsByUserIdAndPromotionCode(@Param("userId") Long userId,
                                         @Param("promotionCode") String promotionCode);

    /**
     * Tìm UserPromotion theo user và promotion
     */
    Optional<UserPromotion> findByUserIdAndPromotionId(Long userId, Long promotionId);

    /**
     * Lấy danh sách khuyến mãi đã sử dụng của user
     */
    Page<UserPromotion> findByUserIdOrderByUsedAtDesc(Long userId, Pageable pageable);

    /**
     * Lấy danh sách người dùng đã sử dụng khuyến mãi cụ thể
     */
    Page<UserPromotion> findByPromotionIdOrderByUsedAtDesc(Long promotionId, Pageable pageable);

    /**
     * Đếm số lượng người dùng đã sử dụng khuyến mãi
     */
    long countByPromotionId(Long promotionId);

    /**
     * Tìm UserPromotion theo order
     */
    Optional<UserPromotion> findByOrderId(Long orderId);

    /**
     * Lấy danh sách UserPromotion trong khoảng thời gian
     */
    @Query("SELECT up FROM UserPromotion up WHERE up.usedAt BETWEEN :startDate AND :endDate")
    List<UserPromotion> findByUsedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Thống kê sử dụng khuyến mãi theo ngày
     */
    @Query("SELECT DATE(up.usedAt) as date, COUNT(up) as count " +
           "FROM UserPromotion up " +
           "WHERE up.usedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(up.usedAt) " +
           "ORDER BY DATE(up.usedAt)")
    List<Object[]> getUsageStatsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Top khuyến mãi được sử dụng nhiều nhất
     */
    @Query("SELECT up.promotion.id, up.promotion.name, COUNT(up) as usageCount " +
           "FROM UserPromotion up " +
           "GROUP BY up.promotion.id, up.promotion.name " +
           "ORDER BY COUNT(up) DESC")
    List<Object[]> getTopUsedPromotions(Pageable pageable);

    /**
     * Xóa tất cả UserPromotion của một khuyến mãi
     */
    void deleteByPromotionId(Long promotionId);

    /**
     * Xóa tất cả UserPromotion của một user
     */
    void deleteByUserId(Long userId);
}