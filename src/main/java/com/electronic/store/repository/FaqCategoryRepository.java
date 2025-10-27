package com.electronic.store.repository;

import com.electronic.store.entity.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqCategoryRepository extends JpaRepository<FaqCategory, Long> {

    // Tìm tất cả categories đang active, sắp xếp theo display_order
    List<FaqCategory> findByStatusOrderByDisplayOrderAsc(FaqCategory.Status status);

    // Tìm tất cả categories đang active
    List<FaqCategory> findByStatus(FaqCategory.Status status);

    // Tìm category theo tên
    FaqCategory findByName(String name);

    // Tìm category theo tên và status
    FaqCategory findByNameAndStatus(String name, FaqCategory.Status status);

    // Đếm số lượng categories theo status
    long countByStatus(FaqCategory.Status status);

    // Query để lấy categories cùng với số lượng FAQs active
    @Query("SELECT c FROM FaqCategory c WHERE c.status = :status ORDER BY c.displayOrder ASC")
    List<FaqCategory> findActiveCategories(FaqCategory.Status status);

    // Tìm categories có FAQs active
    @Query("SELECT DISTINCT c FROM FaqCategory c JOIN c.faqs f WHERE c.status = 'ACTIVE' AND f.status = 'ACTIVE' ORDER BY c.displayOrder ASC")
    List<FaqCategory> findCategoriesWithActiveFaqs();

    // Kiểm tra tồn tại category theo tên
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}