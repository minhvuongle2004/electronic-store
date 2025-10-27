package com.electronic.store.repository;

import com.electronic.store.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Tìm order theo orderId (unique string)
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Tìm order theo orderId với OrderItems eager loaded
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdWithItems(@Param("orderId") String orderId);

    /**
     * Tìm tất cả orders của một user
     */
    Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);

    /**
     * Tìm orders theo status
     */
    Page<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status, Pageable pageable);

    /**
     * Tìm orders theo payment status
     */
    Page<Order> findByPaymentStatusOrderByOrderDateDesc(Order.PaymentStatus paymentStatus, Pageable pageable);

    /**
     * Tìm orders của user theo status
     */
    Page<Order> findByUserIdAndStatusOrderByOrderDateDesc(Long userId, Order.OrderStatus status, Pageable pageable);

    /**
     * Tìm orders của user theo payment status
     */
    Page<Order> findByUserIdAndPaymentStatusOrderByOrderDateDesc(Long userId, Order.PaymentStatus paymentStatus, Pageable pageable);

    /**
     * Đếm số orders của user
     */
    long countByUserId(Long userId);

    /**
     * Đếm số orders theo status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Tìm orders trong khoảng thời gian
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate ORDER BY o.orderDate DESC")
    Page<Order> findByOrderDateBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                       @Param("endDate") java.time.LocalDateTime endDate,
                                       Pageable pageable);

    /**
     * Tìm top orders gần đây của user
     */
    List<Order> findTop10ByUserIdOrderByOrderDateDesc(Long userId);

    /**
     * Tính tổng doanh thu theo status
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
    Double getTotalRevenueByStatus(@Param("status") Order.OrderStatus status);

    /**
     * Kiểm tra order tồn tại theo orderId
     */
    boolean existsByOrderId(String orderId);

    /**
     * Tìm kiếm orders theo tên khách hàng (shipping_name)
     */
    @Query("SELECT o FROM Order o WHERE o.shippingName LIKE %:name% ORDER BY o.orderDate DESC")
    Page<Order> findByShippingNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Tìm kiếm orders theo số điện thoại
     */
    Page<Order> findByShippingPhoneContaining(String phone, Pageable pageable);

    /**
     * Tìm kiếm orders theo orderId pattern
     */
    Page<Order> findByOrderIdContaining(String orderId, Pageable pageable);

    /**
     * Lọc orders theo nhiều tiêu chí
     */
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
           "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) AND " +
           "(:search IS NULL OR o.orderId LIKE %:search% OR o.shippingName LIKE %:search% OR o.shippingPhone LIKE %:search%) " +
           "ORDER BY o.orderDate DESC")
    Page<Order> findOrdersWithFilters(@Param("status") Order.OrderStatus status,
                                      @Param("paymentStatus") Order.PaymentStatus paymentStatus,
                                      @Param("paymentMethod") Order.PaymentMethod paymentMethod,
                                      @Param("search") String search,
                                      Pageable pageable);

    /**
     * Tính tổng doanh thu theo status và user ID
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status AND o.user.id = :userId")
    Double getTotalRevenueByStatusAndUser(@Param("status") Order.OrderStatus status, @Param("userId") Long userId);

    /**
     * Đếm orders theo user ID và status
     */
    long countByUserIdAndStatus(Long userId, Order.OrderStatus status);
    // Đếm đơn hàng theo khoảng thời gian
    long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tính doanh thu theo khoảng thời gian
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.paymentStatus = 'PAID'")
    Double findRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);



    // Top sản phẩm bán chạy (tất cả thời gian)
    @Query("SELECT p.id, p.name, p.imageUrl, SUM(oi.quantity), SUM(oi.quantity * oi.price) " +
            "FROM OrderItem oi JOIN oi.product p JOIN oi.order o " +
            "WHERE o.status != 'CANCELED' " +
            "GROUP BY p.id, p.name, p.imageUrl " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopProducts();

    // Top sản phẩm bán chạy theo khoảng thời gian
    @Query("SELECT p.id, p.name, p.imageUrl, SUM(oi.quantity), SUM(oi.quantity * oi.price) " +
            "FROM OrderItem oi JOIN oi.product p JOIN oi.order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELED' " +
            "GROUP BY p.id, p.name, p.imageUrl " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopProductsByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    // Top khách hàng
    @Query("SELECT u.id, u.fullName, u.email, COUNT(o.id), SUM(o.totalPrice) " +
            "FROM Order o JOIN o.user u " +
            "WHERE o.status != 'CANCELED' " +
            "GROUP BY u.id, u.fullName, u.email " +
            "ORDER BY COUNT(o.id) DESC")
    List<Object[]> findTopCustomers();

    // 5 đơn hàng gần nhất
    List<Order> findTop5ByOrderByOrderDateDesc();
}