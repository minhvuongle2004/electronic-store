package com.electronic.store.service;

import com.electronic.store.entity.Faq;
import com.electronic.store.entity.FaqCategory;
import com.electronic.store.repository.FaqCategoryRepository;
import com.electronic.store.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private FaqCategoryRepository faqCategoryRepository;

    // ===== PUBLIC METHODS FOR USERS =====

    /**
     * Lấy tất cả categories có FAQs active
     */
    public List<FaqCategory> getActiveCategoriesWithFaqs() {
        return faqCategoryRepository.findCategoriesWithActiveFaqs();
    }

    /**
     * Lấy tất cả FAQs active, nhóm theo category
     */
    public List<Faq> getAllActiveFaqs() {
        return faqRepository.findAllActiveFaqs();
    }

    /**
     * Lấy FAQs theo category ID
     */
    public List<Faq> getFaqsByCategory(Long categoryId) {
        return faqRepository.findActiveFaqsByCategoryId(categoryId);
    }

    /**
     * Tìm kiếm FAQs theo từ khóa
     */
    public List<Faq> searchFaqs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveFaqs();
        }
        return faqRepository.searchFaqs(keyword.trim());
    }

    /**
     * Tìm kiếm FAQs trong category cụ thể
     */
    public List<Faq> searchFaqsInCategory(Long categoryId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getFaqsByCategory(categoryId);
        }
        return faqRepository.searchFaqsInCategory(categoryId, keyword.trim());
    }

    /**
     * Lấy FAQs phổ biến
     */
    public List<Faq> getPopularFaqs() {
        return faqRepository.findPopularFaqs();
    }

    // ===== ADMIN METHODS =====

    /**
     * Lấy tất cả categories (bao gồm inactive)
     */
    public List<FaqCategory> getAllCategories() {
        return faqCategoryRepository.findAll();
    }

    /**
     * Lấy category theo ID
     */
    public Optional<FaqCategory> getCategoryById(Long id) {
        return faqCategoryRepository.findById(id);
    }

    /**
     * Tạo mới category
     */
    public FaqCategory createCategory(FaqCategory category) {
        // Validate duplicate name
        if (faqCategoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại: " + category.getName());
        }
        return faqCategoryRepository.save(category);
    }

    /**
     * Cập nhật category
     */
    public FaqCategory updateCategory(Long id, FaqCategory categoryDetails) {
        FaqCategory existingCategory = faqCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Validate duplicate name (exclude current category)
        if (faqCategoryRepository.existsByNameAndIdNot(categoryDetails.getName(), id)) {
            throw new RuntimeException("Tên danh mục đã tồn tại: " + categoryDetails.getName());
        }

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setIcon(categoryDetails.getIcon());
        existingCategory.setDisplayOrder(categoryDetails.getDisplayOrder());
        existingCategory.setStatus(categoryDetails.getStatus());

        return faqCategoryRepository.save(existingCategory);
    }

    /**
     * Xóa category
     */
    public void deleteCategory(Long id) {
        FaqCategory category = faqCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Check if category has FAQs
        long faqCount = faqRepository.countByCategory(category);
        if (faqCount > 0) {
            throw new RuntimeException("Không thể xóa danh mục vì vẫn còn " + faqCount + " câu hỏi");
        }

        faqCategoryRepository.deleteById(id);
    }

    /**
     * Lấy tất cả FAQs (bao gồm inactive)
     */
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    /**
     * Lấy FAQ theo ID
     */
    public Optional<Faq> getFaqById(Long id) {
        return faqRepository.findById(id);
    }

    /**
     * Tạo mới FAQ
     */
    public Faq createFaq(Faq faq) {
        // Validate duplicate question
        if (faqRepository.existsByQuestion(faq.getQuestion())) {
            throw new RuntimeException("Câu hỏi đã tồn tại: " + faq.getQuestion());
        }

        // Validate category exists
        if (faq.getCategory() == null || !faqCategoryRepository.existsById(faq.getCategory().getId())) {
            throw new RuntimeException("Danh mục không tồn tại");
        }

        return faqRepository.save(faq);
    }

    /**
     * Cập nhật FAQ
     */
    public Faq updateFaq(Long id, Faq faqDetails) {
        Faq existingFaq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy FAQ với ID: " + id));

        // Validate duplicate question (exclude current FAQ)
        if (faqRepository.existsByQuestionAndIdNot(faqDetails.getQuestion(), id)) {
            throw new RuntimeException("Câu hỏi đã tồn tại: " + faqDetails.getQuestion());
        }

        // Validate category exists
        if (faqDetails.getCategory() == null || !faqCategoryRepository.existsById(faqDetails.getCategory().getId())) {
            throw new RuntimeException("Danh mục không tồn tại");
        }

        existingFaq.setQuestion(faqDetails.getQuestion());
        existingFaq.setAnswer(faqDetails.getAnswer());
        existingFaq.setCategory(faqDetails.getCategory());
        existingFaq.setDisplayOrder(faqDetails.getDisplayOrder());
        existingFaq.setStatus(faqDetails.getStatus());

        return faqRepository.save(existingFaq);
    }

    /**
     * Xóa FAQ
     */
    public void deleteFaq(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy FAQ với ID: " + id);
        }
        faqRepository.deleteById(id);
    }

    // ===== STATISTICS METHODS =====

    /**
     * Đếm tổng số categories active
     */
    public long countActiveCategories() {
        return faqCategoryRepository.countByStatus(FaqCategory.Status.ACTIVE);
    }

    /**
     * Đếm tổng số FAQs active
     */
    public long countActiveFaqs() {
        return faqRepository.countByStatus(Faq.Status.ACTIVE);
    }

    /**
     * Đếm số FAQs theo category
     */
    public long countFaqsByCategory(Long categoryId) {
        FaqCategory category = faqCategoryRepository.findById(categoryId).orElse(null);
        if (category == null) return 0;
        return faqRepository.countByCategoryAndStatus(category, Faq.Status.ACTIVE);
    }
}