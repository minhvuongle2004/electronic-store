package com.electronic.store.dto.request;

import com.electronic.store.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class UserSearchRequest {

    private User.Role role;

    private User.Status status;

    private String search;

    @Min(value = 0, message = "Page không được nhỏ hơn 0")
    private int page = 0;

    @Min(value = 1, message = "Size phải lớn hơn 0")
    private int size = 10;

    @Pattern(regexp = "^(id|username|email|fullName|createdAt|updatedAt)$",
             message = "Sort field chỉ được phép: id, username, email, fullName, createdAt, updatedAt")
    private String sortBy = "createdAt";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction chỉ được phép: asc, desc")
    private String sortDir = "desc";

    public UserSearchRequest() {}

    public UserSearchRequest(User.Role role, User.Status status, String search, int page, int size) {
        this.role = role;
        this.status = status;
        this.search = search;
        this.page = page;
        this.size = size;
    }

    // Getters and Setters
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}