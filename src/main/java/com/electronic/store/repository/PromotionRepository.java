package com.electronic.store.repository;

import com.electronic.store.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * Tìm kiếm khuyến mãi theo tên (không phân biệt chữ hoa/thường)
     */
    Page<Promotion> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Tìm khuyến mãi theo mã code
     */
    Optional<Promotion> findByCode(String code);

    /**
     * Kiểm tra code có tồn tại
     */
    boolean existsByCode(String code);

    /**
     * Kiểm tra code có tồn tại (trừ ID hiện tại - cho update)
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Lọc khuyến mãi theo trạng thái
     */
    Page<Promotion> findByStatus(Promotion.PromotionStatus status, Pageable pageable);

    /**
     * Lọc khuyến mãi theo loại giảm giá
     */
    Page<Promotion> findByDiscountType(Promotion.DiscountType discountType, Pageable pageable);

    /**
     * Tìm khuyến mãi đang hoạt động theo ngày cụ thể
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= :date AND p.endDate >= :date " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findActivePromotionsByDate(@Param("date") LocalDate date);

    /**
     * Tìm khuyến mãi đang hoạt động theo ngày cụ thể (có phân trang)
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= :date AND p.endDate >= :date " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    Page<Promotion> findActivePromotionsByDate(@Param("date") LocalDate date, Pageable pageable);

    /**
     * Tìm khuyến mãi theo khoảng thời gian
     */
    @Query("SELECT p FROM Promotion p WHERE " +
           "(p.startDate BETWEEN :startDate AND :endDate) OR " +
           "(p.endDate BETWEEN :startDate AND :endDate) OR " +
           "(p.startDate <= :startDate AND p.endDate >= :endDate)")
    Page<Promotion> findByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);

    /**
     * Tìm khuyến mãi có thể sử dụng cho người dùng cụ thể
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit) " +
           "AND p.id NOT IN (SELECT up.promotion.id FROM UserPromotion up WHERE up.user.id = :userId)")
    List<Promotion> findAvailablePromotionsForUser(@Param("userId") Long userId);

    /**
     * Tìm khuyến mãi theo điều kiện tìm kiếm phức tạp
     */
    @Query("SELECT p FROM Promotion p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:discountType IS NULL OR p.discountType = :discountType) " +
           "AND (:startDate IS NULL OR p.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.endDate <= :endDate)")
    Page<Promotion> findWithFilters(
            @Param("name") String name,
            @Param("code") String code,
            @Param("status") Promotion.PromotionStatus status,
            @Param("discountType") Promotion.DiscountType discountType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Đếm số lượng khuyến mãi theo trạng thái
     */
    long countByStatus(Promotion.PromotionStatus status);

    /**
     * Tìm khuyến mãi sắp hết hạn (trong N ngày tới)
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.endDate BETWEEN CURRENT_DATE AND :futureDate")
    List<Promotion> findExpiringPromotions(@Param("futureDate") LocalDate futureDate);

    /**
     * Tìm tất cả khuyến mãi đang hoạt động
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findActivePromotions();
}