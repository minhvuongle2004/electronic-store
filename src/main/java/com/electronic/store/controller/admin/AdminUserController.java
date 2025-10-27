package com.electronic.store.controller.admin;

import com.electronic.store.dto.request.BulkDeleteUsersRequest;
import com.electronic.store.dto.request.UpdateUserStatusRequest;
import com.electronic.store.dto.request.UserSearchRequest;
import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.dto.response.PagedResponse;
import com.electronic.store.dto.response.UserDetailResponse;
import com.electronic.store.dto.response.UserListResponse;
import com.electronic.store.entity.User;
import com.electronic.store.service.AdminUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminUserService;

    /**
     * Lấy danh sách users với filter, search và pagination
     * GET /admin/users?role=USER&status=ACTIVE&search=john&page=0&size=10&sortBy=createdAt&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserListResponse>>> getUsers(
            @RequestParam(required = false) User.Role role,
            @RequestParam(required = false) User.Status status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Validate sort parameters
            if (!isValidSortField(sortBy)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Sort field không hợp lệ: " + sortBy));
            }

            if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Sort direction chỉ được phép: asc, desc"));
            }

            Page<User> userPage = adminUserService.getUsers(role, status, search, page, size, sortBy, sortDir);

            List<UserListResponse> userResponses = userPage.getContent().stream()
                    .map(UserListResponse::new)
                    .collect(Collectors.toList());

            PagedResponse<UserListResponse> pagedResponse = PagedResponse.of(userPage, userResponses);

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", pagedResponse));

        } catch (Exception e) {
            logger.error("Error getting users list", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết user theo ID
     * GET /admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Long id) {
        try {
            ApiResponse<User> response = adminUserService.getUserById(id);

            if (!response.isSuccess()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(response.getMessage()));
            }

            UserDetailResponse userDetail = new UserDetailResponse(response.getData());

            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userDetail));

        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật trạng thái user (ACTIVE/BLOCKED)
     * PUT /admin/users/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        try {
            ApiResponse<String> response = adminUserService.updateUserStatus(id, request.getStatus());

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error updating user status: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Xóa user (soft delete)
     * DELETE /admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        try {
            ApiResponse<String> response = adminUserService.deleteUser(id);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error deleting user: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Xóa nhiều users cùng lúc
     * DELETE /admin/users/bulk
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> deleteMultipleUsers(
            @Valid @RequestBody BulkDeleteUsersRequest request) {

        try {
            ApiResponse<String> response = adminUserService.deleteMultipleUsers(request.getUserIds());

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error bulk deleting users", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê users
     * GET /admin/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        try {
            ApiResponse<Map<String, Object>> response = adminUserService.getUserStats();

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error getting user stats", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Tìm kiếm users
     * GET /admin/users/search?keyword=john&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<UserListResponse>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Keyword không được để trống"));
            }

            Page<User> userPage = adminUserService.searchUsers(keyword.trim(), page, size);

            List<UserListResponse> userResponses = userPage.getContent().stream()
                    .map(UserListResponse::new)
                    .collect(Collectors.toList());

            PagedResponse<UserListResponse> pagedResponse = PagedResponse.of(userPage, userResponses);

            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm người dùng thành công", pagedResponse));

        } catch (Exception e) {
            logger.error("Error searching users with keyword: {}", keyword, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Lấy top users có nhiều orders nhất
     * GET /admin/users/top-buyers?page=0&size=10
     */
    @GetMapping("/top-buyers")
    public ResponseEntity<ApiResponse<PagedResponse<UserListResponse>>> getTopUsersByOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<User> userPage = adminUserService.getTopUsersByOrders(page, size);

            List<UserListResponse> userResponses = userPage.getContent().stream()
                    .map(UserListResponse::new)
                    .collect(Collectors.toList());

            PagedResponse<UserListResponse> pagedResponse = PagedResponse.of(userPage, userResponses);

            return ResponseEntity.ok(ApiResponse.success("Lấy top khách hàng thành công", pagedResponse));

        } catch (Exception e) {
            logger.error("Error getting top users by orders", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Validate sort field
     */
    private boolean isValidSortField(String sortBy) {
        return List.of("id", "username", "email", "fullName", "createdAt", "updatedAt")
                .contains(sortBy);
    }
}