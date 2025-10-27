package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Faq;
import com.electronic.store.entity.FaqCategory;
import com.electronic.store.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FaqController {

    @Autowired
    private FaqService faqService;

    /**
     * Lấy tất cả categories có FAQs active
     * GET /api/faq/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<FaqCategory>>> getActiveCategories() {
        try {
            List<FaqCategory> categories = faqService.getActiveCategoriesWithFaqs();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách danh mục thành công", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy danh sách danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy tất cả FAQs active
     * GET /api/faq
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Faq>>> getAllActiveFaqs() {
        try {
            List<Faq> faqs = faqService.getAllActiveFaqs();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách FAQ thành công", faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy danh sách FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy FAQs theo category
     * GET /api/faq/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Faq>>> getFaqsByCategory(@PathVariable Long categoryId) {
        try {
            List<Faq> faqs = faqService.getFaqsByCategory(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy FAQ theo danh mục thành công", faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy FAQ theo danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Tìm kiếm FAQs
     * GET /api/faq/search?q=keyword
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Faq>>> searchFaqs(@RequestParam("q") String keyword) {
        try {
            List<Faq> faqs = faqService.searchFaqs(keyword);
            String message = faqs.isEmpty() ?
                "Không tìm thấy FAQ nào với từ khóa: " + keyword :
                "Tìm thấy " + faqs.size() + " FAQ";
            return ResponseEntity.ok(new ApiResponse<>(true, message, faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi tìm kiếm FAQ: " + e.getMessage(), null));
        }
    }

    /**
     * Tìm kiếm FAQs trong category cụ thể
     * GET /api/faq/category/{categoryId}/search?q=keyword
     */
    @GetMapping("/category/{categoryId}/search")
    public ResponseEntity<ApiResponse<List<Faq>>> searchFaqsInCategory(
            @PathVariable Long categoryId,
            @RequestParam("q") String keyword) {
        try {
            List<Faq> faqs = faqService.searchFaqsInCategory(categoryId, keyword);
            String message = faqs.isEmpty() ?
                "Không tìm thấy FAQ nào trong danh mục này với từ khóa: " + keyword :
                "Tìm thấy " + faqs.size() + " FAQ";
            return ResponseEntity.ok(new ApiResponse<>(true, message, faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi tìm kiếm FAQ trong danh mục: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy FAQs phổ biến
     * GET /api/faq/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Faq>>> getPopularFaqs() {
        try {
            List<Faq> faqs = faqService.getPopularFaqs();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy FAQ phổ biến thành công", faqs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy FAQ phổ biến: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy thống kê FAQ
     * GET /api/faq/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getFaqStats() {
        try {
            long totalCategories = faqService.countActiveCategories();
            long totalFaqs = faqService.countActiveFaqs();

            var stats = new java.util.HashMap<String, Object>();
            stats.put("totalCategories", totalCategories);
            stats.put("totalFaqs", totalFaqs);

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy thống kê: " + e.getMessage(), null));
        }
    }
}