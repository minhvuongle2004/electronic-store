package com.electronic.store.service;

import com.electronic.store.entity.Promotion;
import com.electronic.store.repository.PromotionRepository;
import com.electronic.store.repository.UserPromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private UserPromotionRepository userPromotionRepository;

    /**
     * Lấy tất cả khuyến mãi với phân trang (cho admin)
     */
    public Page<Promotion> getAllPromotions(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findAll(pageable);
    }

    /**
     * Lấy khuyến mãi theo ID
     */
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    /**
     * Lấy khuyến mãi theo code
     */
    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    /**
     * Tìm kiếm khuyến mãi theo tên
     */
    public Page<Promotion> searchPromotionsByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Lọc khuyến mãi theo trạng thái
     */
    public Page<Promotion> getPromotionsByStatus(Promotion.PromotionStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findByStatus(status, pageable);
    }

    /**
     * Lọc khuyến mãi theo loại giảm giá
     */
    public Page<Promotion> getPromotionsByDiscountType(Promotion.DiscountType discountType, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findByDiscountType(discountType, pageable);
    }

    /**
     * Lọc khuyến mãi đang hoạt động theo ngày cụ thể
     */
    public Page<Promotion> getActivePromotionsByDate(LocalDate date, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findActivePromotionsByDate(date, pageable);
    }

    /**
     * Lọc khuyến mãi theo khoảng thời gian
     */
    public Page<Promotion> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Tìm kiếm khuyến mãi với filters phức tạp
     */
    public Page<Promotion> searchPromotionsWithFilters(
            String name, String code, Promotion.PromotionStatus status,
            Promotion.DiscountType discountType, LocalDate startDate, LocalDate endDate, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return promotionRepository.findWithFilters(name, code, status, discountType, startDate, endDate, pageable);
    }

    /**
     * Thêm khuyến mãi mới
     */
    public Promotion createPromotion(Promotion promotion) {
        // Kiểm tra code đã tồn tại
        if (promotion.getCode() != null && promotionRepository.existsByCode(promotion.getCode())) {
            throw new RuntimeException("Mã khuyến mãi '" + promotion.getCode() + "' đã tồn tại");
        }

        // Validate ngày
        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu không thể sau ngày kết thúc");
        }

        // Validate discount value
        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT) {
            if (promotion.getDiscountValue() <= 0 || promotion.getDiscountValue() > 100) {
                throw new RuntimeException("Phần trăm giảm giá phải từ 0 đến 100");
            }
        } else {
            if (promotion.getDiscountValue() <= 0) {
                throw new RuntimeException("Giá trị giảm giá phải lớn hơn 0");
            }
        }

        // Validate max discount amount (chỉ cho PERCENT)
        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT &&
            promotion.getMaxDiscountAmount() != null && promotion.getMaxDiscountAmount() <= 0) {
            throw new RuntimeException("Số tiền giảm tối đa phải lớn hơn 0");
        }

        // Validate min order amount
        if (promotion.getMinOrderAmount() != null && promotion.getMinOrderAmount() < 0) {
            throw new RuntimeException("Giá trị đơn hàng tối thiểu không thể âm");
        }

        // Validate usage limit
        if (promotion.getUsageLimit() != null && promotion.getUsageLimit() <= 0) {
            throw new RuntimeException("Giới hạn sử dụng phải lớn hơn 0");
        }

        return promotionRepository.save(promotion);
    }

    /**
     * Cập nhật khuyến mãi
     */
    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);

        if (optionalPromotion.isEmpty()) {
            throw new RuntimeException("Khuyến mãi không tồn tại với ID: " + id);
        }

        Promotion existingPromotion = optionalPromotion.get();

        // Kiểm tra code trùng lặp (trừ promotion hiện tại)
        if (promotionDetails.getCode() != null &&
            !promotionDetails.getCode().equals(existingPromotion.getCode()) &&
            promotionRepository.existsByCode(promotionDetails.getCode())) {
            throw new RuntimeException("Mã khuyến mãi '" + promotionDetails.getCode() + "' đã tồn tại");
        }

        // Validate ngày
        if (promotionDetails.getStartDate().isAfter(promotionDetails.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu không thể sau ngày kết thúc");
        }

        // Validate discount value
        if (promotionDetails.getDiscountType() == Promotion.DiscountType.PERCENT) {
            if (promotionDetails.getDiscountValue() <= 0 || promotionDetails.getDiscountValue() > 100) {
                throw new RuntimeException("Phần trăm giảm giá phải từ 0 đến 100");
            }
        } else {
            if (promotionDetails.getDiscountValue() <= 0) {
                throw new RuntimeException("Giá trị giảm giá phải lớn hơn 0");
            }
        }

        // Validate max discount amount (chỉ cho PERCENT)
        if (promotionDetails.getDiscountType() == Promotion.DiscountType.PERCENT &&
            promotionDetails.getMaxDiscountAmount() != null && promotionDetails.getMaxDiscountAmount() <= 0) {
            throw new RuntimeException("Số tiền giảm tối đa phải lớn hơn 0");
        }

        // Validate min order amount
        if (promotionDetails.getMinOrderAmount() != null && promotionDetails.getMinOrderAmount() < 0) {
            throw new RuntimeException("Giá trị đơn hàng tối thiểu không thể âm");
        }

        // Validate usage limit
        if (promotionDetails.getUsageLimit() != null && promotionDetails.getUsageLimit() <= 0) {
            throw new RuntimeException("Giới hạn sử dụng phải lớn hơn 0");
        }

        // Cập nhật thông tin
        existingPromotion.setName(promotionDetails.getName());
        existingPromotion.setCode(promotionDetails.getCode());
        existingPromotion.setDescription(promotionDetails.getDescription());
        existingPromotion.setDiscountType(promotionDetails.getDiscountType());
        existingPromotion.setDiscountValue(promotionDetails.getDiscountValue());
        existingPromotion.setMaxDiscountAmount(promotionDetails.getMaxDiscountAmount());
        existingPromotion.setMinOrderAmount(promotionDetails.getMinOrderAmount());
        existingPromotion.setUsageLimit(promotionDetails.getUsageLimit());
        existingPromotion.setStartDate(promotionDetails.getStartDate());
        existingPromotion.setEndDate(promotionDetails.getEndDate());
        existingPromotion.setStatus(promotionDetails.getStatus());

        return promotionRepository.save(existingPromotion);
    }

    /**
     * Thay đổi trạng thái khuyến mãi
     */
    public Promotion changePromotionStatus(Long id, Promotion.PromotionStatus status) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);

        if (optionalPromotion.isEmpty()) {
            throw new RuntimeException("Khuyến mãi không tồn tại với ID: " + id);
        }

        Promotion promotion = optionalPromotion.get();
        promotion.setStatus(status);
        return promotionRepository.save(promotion);
    }

    /**
     * Xóa khuyến mãi
     */
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Khuyến mãi không tồn tại với ID: " + id);
        }

        // Kiểm tra khuyến mãi đã được sử dụng chưa
        Optional<Promotion> promotion = promotionRepository.findById(id);
        if (promotion.isPresent() && promotion.get().getUsedCount() > 0) {
            throw new RuntimeException("Không thể xóa khuyến mãi đã được sử dụng");
        }

        promotionRepository.deleteById(id);
    }

    /**
     * Kiểm tra khuyến mãi tồn tại theo ID
     */
    public boolean existsById(Long id) {
        return promotionRepository.existsById(id);
    }

    /**
     * Kiểm tra code đã tồn tại
     */
    public boolean existsByCode(String code) {
        return promotionRepository.existsByCode(code);
    }

    /**
     * Đếm số lượng khuyến mãi theo trạng thái
     */
    public long countByStatus(Promotion.PromotionStatus status) {
        return promotionRepository.countByStatus(status);
    }

    /**
     * Lấy danh sách khuyến mãi sắp hết hạn (trong 7 ngày tới)
     */
    public List<Promotion> getExpiringPromotions() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        return promotionRepository.findExpiringPromotions(futureDate);
    }

    /**
     * Lấy danh sách mã giảm giá có thể áp dụng cho đơn hàng
     */
    public List<Promotion> getAvailablePromotionsForOrder(Double orderAmount, Long userId) {
        List<Promotion> allActivePromotions = promotionRepository.findActivePromotions();

        return allActivePromotions.stream()
                .filter(promotion -> promotion.isActive())
                .filter(promotion -> promotion.getMinOrderAmount() <= orderAmount)
                .filter(promotion -> canUserUsePromotion(promotion.getId(), userId))
                .toList();
    }

    /**
     * Kiểm tra user có thể sử dụng promotion này không
     */
    public boolean canUserUsePromotion(Long promotionId, Long userId) {
        if (userId == null || userId == 0L) {
            return true; // Guest user có thể sử dụng
        }
        return !userPromotionRepository.existsByUserIdAndPromotionId(userId, promotionId);
    }

    /**
     * Áp dụng mã giảm giá vào đơn hàng
     */
    public Double applyPromotion(String promotionCode, Double orderAmount, Long userId) {
        Optional<Promotion> promotionOpt = promotionRepository.findByCode(promotionCode);

        if (promotionOpt.isEmpty()) {
            throw new RuntimeException("Mã giảm giá không tồn tại");
        }

        Promotion promotion = promotionOpt.get();

        if (!promotion.isActive()) {
            throw new RuntimeException("Mã giảm giá không còn hiệu lực");
        }

        // Kiểm tra user đã sử dụng promotion này chưa qua UserPromotionRepository
        if (userId != null && userId > 0L) {
            if (userPromotionRepository.existsByUserIdAndPromotionCode(userId, promotionCode)) {
                throw new RuntimeException("Bạn đã sử dụng mã giảm giá này");
            }
        }

        if (orderAmount < promotion.getMinOrderAmount()) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + promotion.getMinOrderAmount() + " VND");
        }

        return promotion.calculateDiscount(orderAmount);
    }

    /**
     * Validate mã giảm giá có thể sử dụng
     */
    public boolean validatePromotionForOrder(String promotionCode, Double orderAmount, Long userId) {
        try {
            applyPromotion(promotionCode, orderAmount, userId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}