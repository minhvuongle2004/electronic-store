package com.electronic.store.repository;

import com.electronic.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Tìm kiếm sản phẩm theo tên (không phân biệt chữ hoa/thường)
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Lọc sản phẩm theo danh mục
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Kiểm tra sản phẩm có tồn tại theo tên
     */
    boolean existsByName(String name);

    /**
     * Kiểm tra sản phẩm có tồn tại theo tên (trừ ID hiện tại - cho update)
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Lấy sản phẩm cùng danh mục nhưng loại trừ sản phẩm hiện tại
     */
    Page<Product> findByCategoryIdAndIdNot(Long categoryId, Long excludeId, Pageable pageable);

    // Đếm sản phẩm sắp hết hàng
    long countByStockLessThan(int stock);
}