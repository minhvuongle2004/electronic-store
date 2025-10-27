package com.electronic.store.repository;

import com.electronic.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Tìm tất cả order items của một order
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Tìm order items theo product ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);

    /**
     * Tìm order items của user
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId ORDER BY oi.order.orderDate DESC")
    List<OrderItem> findByUserId(@Param("userId") Long userId);

    /**
     * Đếm số lượng order items của một order
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    long countByOrderId(@Param("orderId") Long orderId);

    /**
     * Tính tổng số lượng sản phẩm đã bán
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.status = 'COMPLETED'")
    Long getTotalSoldQuantityByProductId(@Param("productId") Long productId);

    /**
     * Tìm top sản phẩm bán chạy
     */
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
           "WHERE oi.order.status = 'COMPLETED' " +
           "GROUP BY oi.product.id " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();

    /**
     * Tính doanh thu theo sản phẩm
     */
    @Query("SELECT SUM(oi.quantity * oi.price) FROM OrderItem oi " +
           "WHERE oi.product.id = :productId AND oi.order.status = 'COMPLETED'")
    Double getRevenueByProductId(@Param("productId") Long productId);

    /**
     * Tìm order items theo order status
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.status = :status ORDER BY oi.order.orderDate DESC")
    List<OrderItem> findByOrderStatus(@Param("status") com.electronic.store.entity.Order.OrderStatus status);

    /**
     * Xóa tất cả order items của một order
     */
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}