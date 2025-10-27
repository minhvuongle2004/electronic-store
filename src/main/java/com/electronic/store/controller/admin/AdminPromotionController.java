package com.electronic.store.controller.admin;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Promotion;
import com.electronic.store.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/promotions")
@CrossOrigin(origins = "*")
public class AdminPromotionController {

    @Autowired
    private PromotionService promotionService;

    /**
     * Lấy tất cả khuyến mãi với phân trang
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Promotion>>> getAllPromotions(
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.getAllPromotions(page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách khuyến mãi thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy danh sách khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy khuyến mãi theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Promotion>> getPromotionById(@PathVariable Long id) {
        try {
            Optional<Promotion> promotion = promotionService.getPromotionById(id);
            if (promotion.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Lấy thông tin khuyến mãi thành công", promotion.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Không tìm thấy khuyến mãi với ID: " + id, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy thông tin khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy khuyến mãi theo code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<Promotion>> getPromotionByCode(@PathVariable String code) {
        try {
            Optional<Promotion> promotion = promotionService.getPromotionByCode(code);
            if (promotion.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Lấy thông tin khuyến mãi thành công", promotion.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Không tìm thấy khuyến mãi với mã: " + code, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy thông tin khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Tìm kiếm khuyến mãi theo tên
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Promotion>>> searchPromotions(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.searchPromotionsByName(name, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Tìm kiếm khuyến mãi thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi tìm kiếm khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lọc khuyến mãi theo trạng thái
     */
    @GetMapping("/filter/status")
    public ResponseEntity<ApiResponse<Page<Promotion>>> getPromotionsByStatus(
            @RequestParam Promotion.PromotionStatus status,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.getPromotionsByStatus(status, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lọc khuyến mãi theo trạng thái thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lọc khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lọc khuyến mãi theo loại giảm giá
     */
    @GetMapping("/filter/discount-type")
    public ResponseEntity<ApiResponse<Page<Promotion>>> getPromotionsByDiscountType(
            @RequestParam Promotion.DiscountType discountType,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.getPromotionsByDiscountType(discountType, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lọc khuyến mãi theo loại giảm giá thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lọc khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lọc khuyến mãi đang hoạt động theo ngày cụ thể
     */
    @GetMapping("/filter/active-by-date")
    public ResponseEntity<ApiResponse<Page<Promotion>>> getActivePromotionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.getActivePromotionsByDate(date, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lọc khuyến mãi theo ngày thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lọc khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lọc khuyến mãi theo khoảng thời gian
     */
    @GetMapping("/filter/date-range")
    public ResponseEntity<ApiResponse<Page<Promotion>>> getPromotionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.getPromotionsByDateRange(startDate, endDate, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lọc khuyến mãi theo khoảng thời gian thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lọc khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Tìm kiếm khuyến mãi với filters phức tạp
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<ApiResponse<Page<Promotion>>> advancedSearchPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Promotion.PromotionStatus status,
            @RequestParam(required = false) Promotion.DiscountType discountType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Promotion> promotions = promotionService.searchPromotionsWithFilters(
                name, code, status, discountType, startDate, endDate, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Tìm kiếm nâng cao thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi tìm kiếm nâng cao: " + e.getMessage(), null));
        }
    }

    /**
     * Thêm khuyến mãi mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(@Valid @RequestBody Promotion promotion) {
        try {
            Promotion newPromotion = promotionService.createPromotion(promotion);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo khuyến mãi thành công", newPromotion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi tạo khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Cập nhật khuyến mãi
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(
            @PathVariable Long id, @Valid @RequestBody Promotion promotion) {
        try {
            Promotion updatedPromotion = promotionService.updatePromotion(id, promotion);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Cập nhật khuyến mãi thành công", updatedPromotion)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi cập nhật khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Thay đổi trạng thái khuyến mãi
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Promotion>> changePromotionStatus(
            @PathVariable Long id, @RequestParam Promotion.PromotionStatus status) {
        try {
            Promotion updatedPromotion = promotionService.changePromotionStatus(id, status);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Thay đổi trạng thái khuyến mãi thành công", updatedPromotion)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi thay đổi trạng thái: " + e.getMessage(), null));
        }
    }

    /**
     * Xóa khuyến mãi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Xóa khuyến mãi thành công", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi xóa khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Kiểm tra code khuyến mãi có tồn tại
     */
    @GetMapping("/check-code/{code}")
    public ResponseEntity<ApiResponse<Boolean>> checkCodeExists(@PathVariable String code) {
        try {
            boolean exists = promotionService.existsByCode(code);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Kiểm tra mã thành công", exists)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi kiểm tra mã: " + e.getMessage(), null));
        }
    }

    /**
     * Đếm số lượng khuyến mãi theo trạng thái
     */
    @GetMapping("/count/status")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@RequestParam Promotion.PromotionStatus status) {
        try {
            long count = promotionService.countByStatus(status);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Đếm khuyến mãi thành công", count)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi đếm khuyến mãi: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy danh sách khuyến mãi sắp hết hạn
     */
    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<Promotion>>> getExpiringPromotions() {
        try {
            List<Promotion> promotions = promotionService.getExpiringPromotions();
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách khuyến mãi sắp hết hạn thành công", promotions)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy danh sách: " + e.getMessage(), null));
        }
    }
}