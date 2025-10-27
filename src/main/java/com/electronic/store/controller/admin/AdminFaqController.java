package com.electronic.store.controller.admin;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Faq;
import com.electronic.store.entity.FaqCategory;
import com.electronic.store.service.FaqService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/faq")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminFaqController {

    @Autowired
    private FaqService faqService;

    // ===== FAQ CATEGORY MANAGEMENT =====

    /**
     * Lấy tất cả categories (bao gồm inactive)
     * GET /api/admin/faq/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<FaqCategory>>> getAllCategories() {
        try {
            List<FaqCategory> categories = faqService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách danh mục thành công", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy danh sách danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy category theo ID
     * GET /api/admin/faq/categories/{id}
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<FaqCategory>> getCategoryById(@PathVariable Long id) {
        try {
            Optional<FaqCategory> category = faqService.getCategoryById(id);
            if (category.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin danh mục thành công", category.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy thông tin danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Tạo mới category
     * POST /api/admin/faq/categories
     */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<FaqCategory>> createCategory(@Valid @RequestBody FaqCategory category) {
        try {
            FaqCategory createdCategory = faqService.createCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo danh mục thành công", createdCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi tạo danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Cập nhật category
     * PUT /api/admin/faq/categories/{id}
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<FaqCategory>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody FaqCategory categoryDetails) {
        try {
            FaqCategory updatedCategory = faqService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật danh mục thành công", updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi cập nhật danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Xóa category
     * DELETE /api/admin/faq/categories/{id}
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        try {
            faqService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa danh mục thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi xóa danh mục: " + e.getMessage(), null));
        }
    }

    // ===== FAQ MANAGEMENT =====

    /**
     * Lấy tất cả FAQs (bao gồm inactive)
     * GET /api/admin/faq/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<Faq>>> getAllFaqs() {
        try {
            List<Faq> faqs = faqService.getAllFaqs();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách FAQ thành công", faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy danh sách FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy FAQ theo ID
     * GET /api/admin/faq/questions/{id}
     */
    @GetMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Faq>> getFaqById(@PathVariable Long id) {
        try {
            Optional<Faq> faq = faqService.getFaqById(id);
            if (faq.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin FAQ thành công", faq.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy thông tin FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Tạo mới FAQ
     * POST /api/admin/faq/questions
     */
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<Faq>> createFaq(@Valid @RequestBody Faq faq) {
        try {
            Faq createdFaq = faqService.createFaq(faq);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo FAQ thành công", createdFaq));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi tạo FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Cập nhật FAQ
     * PUT /api/admin/faq/questions/{id}
     */
    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Faq>> updateFaq(@PathVariable Long id, @Valid @RequestBody Faq faqDetails) {
        try {
            Faq updatedFaq = faqService.updateFaq(id, faqDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật FAQ thành công", updatedFaq));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi cập nhật FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Xóa FAQ
     * DELETE /api/admin/faq/questions/{id}
     */
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFaq(@PathVariable Long id) {
        try {
            faqService.deleteFaq(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa FAQ thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi xóa FAQ: " + e.getMessage(), null));
        }
    }

    // ===== STATISTICS =====

    /**
     * Lấy thống kê tổng quan
     * GET /api/admin/faq/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getAdminStats() {
        try {
            long totalCategories = faqService.countActiveCategories();
            long totalFaqs = faqService.countActiveFaqs();

            var stats = new java.util.HashMap<String, Object>();
            stats.put("totalCategories", totalCategories);
            stats.put("totalFaqs", totalFaqs);
            stats.put("allCategories", faqService.getAllCategories().size());
            stats.put("allFaqs", faqService.getAllFaqs().size());

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy thống kê: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy số lượng FAQs theo category
     * GET /api/admin/faq/categories/{categoryId}/count
     */
    @GetMapping("/categories/{categoryId}/count")
    public ResponseEntity<ApiResponse<Long>> getFaqCountByCategory(@PathVariable Long categoryId) {
        try {
            long count = faqService.countFaqsByCategory(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy số lượng FAQ thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi đếm FAQ: " + e.getMessage(), null));
        }
    }
}