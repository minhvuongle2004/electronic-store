package com.electronic.store.repository;

import com.electronic.store.entity.Faq;
import com.electronic.store.entity.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    // Tìm tất cả FAQs đang active, sắp xếp theo category và display_order
    @Query("SELECT f FROM Faq f WHERE f.status = 'ACTIVE' ORDER BY f.category.displayOrder ASC, f.displayOrder ASC")
    List<Faq> findAllActiveFaqs();

    // Tìm FAQs theo category
    List<Faq> findByCategoryOrderByDisplayOrderAsc(FaqCategory category);

    // Tìm FAQs theo category và status
    List<Faq> findByCategoryAndStatusOrderByDisplayOrderAsc(FaqCategory category, Faq.Status status);

    // Tìm FAQs theo category ID
    @Query("SELECT f FROM Faq f WHERE f.category.id = :categoryId AND f.status = 'ACTIVE' ORDER BY f.displayOrder ASC")
    List<Faq> findActiveFaqsByCategoryId(@Param("categoryId") Long categoryId);

    // Tìm FAQs theo status
    List<Faq> findByStatusOrderByDisplayOrderAsc(Faq.Status status);

    // Tìm kiếm FAQ theo từ khóa trong question hoặc answer
    @Query("SELECT f FROM Faq f WHERE f.status = 'ACTIVE' AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY f.category.displayOrder ASC, f.displayOrder ASC")
    List<Faq> searchFaqs(@Param("keyword") String keyword);

    // Tìm kiếm FAQ theo từ khóa trong category cụ thể
    @Query("SELECT f FROM Faq f WHERE f.category.id = :categoryId AND f.status = 'ACTIVE' AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY f.displayOrder ASC")
    List<Faq> searchFaqsInCategory(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    // Đếm số lượng FAQs theo category
    long countByCategory(FaqCategory category);

    // Đếm số lượng FAQs theo category và status
    long countByCategoryAndStatus(FaqCategory category, Faq.Status status);

    // Đếm số lượng FAQs theo status
    long countByStatus(Faq.Status status);

    // Lấy FAQs phổ biến (top questions) - có thể mở rộng sau này
    @Query("SELECT f FROM Faq f WHERE f.status = 'ACTIVE' ORDER BY f.displayOrder ASC")
    List<Faq> findPopularFaqs();

    // Tìm FAQ theo question (để tránh duplicate)
    Faq findByQuestion(String question);

    boolean existsByQuestion(String question);

    boolean existsByQuestionAndIdNot(String question, Long id);
}