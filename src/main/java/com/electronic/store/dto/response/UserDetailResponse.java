package com.electronic.store.dto.response;

import com.electronic.store.entity.User;
import java.time.LocalDateTime;

public class UserDetailResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private User.Role role;
    private User.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thống kê bổ sung
    private int totalOrders;
    private int totalCartItems;
    private int totalWishlistItems;

    public UserDetailResponse() {}

    public UserDetailResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        // Tính toán thống kê từ relationships
        this.totalOrders = user.getOrders() != null ? user.getOrders().size() : 0;
        this.totalCartItems = user.getCartItems() != null ? user.getCartItems().size() : 0;
        this.totalWishlistItems = user.getWishlistItems() != null ? user.getWishlistItems().size() : 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public User.Status getStatus() {
        return status;
    }

    public void setStatus(User.Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalCartItems() {
        return totalCartItems;
    }

    public void setTotalCartItems(int totalCartItems) {
        this.totalCartItems = totalCartItems;
    }

    public int getTotalWishlistItems() {
        return totalWishlistItems;
    }

    public void setTotalWishlistItems(int totalWishlistItems) {
        this.totalWishlistItems = totalWishlistItems;
    }
}