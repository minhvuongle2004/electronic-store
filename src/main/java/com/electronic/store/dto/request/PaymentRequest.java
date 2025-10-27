package com.electronic.store.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class PaymentRequest {

    // User ID sẽ được lấy từ session, không cần frontend gửi
    private Long userId;

    @NotNull(message = "Tổng tiền không được để trống")
    @Positive(message = "Tổng tiền phải lớn hơn 0")
    private Double totalAmount;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<CartItem> items;

    private String customerInfo;
    private String note;

    // Shipping information
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;

    // Promotion information
    private String promotionCode;
    private Double discountAmount;

    // Constructors
    public PaymentRequest() {}

    public PaymentRequest(Long userId, Double totalAmount, List<CartItem> items) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    // Inner class for cart items
    public static class CartItem {
        @NotNull(message = "Product ID không được để trống")
        private Long productId;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotNull(message = "Giá không được để trống")
        @Positive(message = "Giá phải lớn hơn 0")
        private Double price;

        private String productName;

        // Constructors
        public CartItem() {}

        public CartItem(Long productId, Integer quantity, Double price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}