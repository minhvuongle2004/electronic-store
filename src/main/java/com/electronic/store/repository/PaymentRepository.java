package com.electronic.store.repository;

import com.electronic.store.entity.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tìm payment theo orderId
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Tìm payment theo transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Tìm tất cả payments của một order
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC")
    List<Payment> findAllByOrderId(@Param("orderId") Long orderId);

    /**
     * Tìm payments theo status
     */
    Page<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status, Pageable pageable);

    /**
     * Tìm payments của user
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Tìm payments của user theo status
     */
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Payment> findByUserIdAndStatus(@Param("userId") Long userId,
                                       @Param("status") Payment.PaymentStatus status,
                                       Pageable pageable);

    /**
     * Tìm payments trong khoảng thời gian
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate ORDER BY p.createdAt DESC")
    Page<Payment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    /**
     * Đếm số payments theo status
     */
    long countByStatus(Payment.PaymentStatus status);

    /**
     * Tính tổng số tiền theo status
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    Double getTotalAmountByStatus(@Param("status") Payment.PaymentStatus status);

    /**
     * Tính tổng số tiền theo status trong khoảng thời gian
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    Double getTotalAmountByStatusAndDateRange(@Param("status") Payment.PaymentStatus status,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Kiểm tra payment tồn tại theo transaction ID
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Tìm payments gần đây
     */
    List<Payment> findTop10ByOrderByCreatedAtDesc();

    /**
     * Tìm pending payments cũ (để cleanup)
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findOldPendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);
}