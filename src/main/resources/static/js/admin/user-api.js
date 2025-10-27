/**
 * Admin User Management API Functions
 * Handles all API calls for user management features
 */

class AdminUserAPI {
    constructor() {
        this.baseURL = '/api/admin/users';
        this.setupAxiosDefaults();
    }

    // Setup axios defaults
    setupAxiosDefaults() {
        // Set default content type
        axios.defaults.headers.common['Content-Type'] = 'application/json';

        // Add auth token if available
        const token = localStorage.getItem('token');
        if (token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }

        // Add request interceptor
        axios.interceptors.request.use(
            config => {
                console.log('Making API request:', config.method?.toUpperCase(), config.url);
                return config;
            },
            error => {
                console.error('Request error:', error);
                return Promise.reject(error);
            }
        );

        // Add response interceptor
        axios.interceptors.response.use(
            response => {
                console.log('API response:', response.status, response.config.url);
                return response;
            },
            error => {
                console.error('Response error:', error.response?.status, error.config?.url);

                // Handle 401 Unauthorized
                if (error.response?.status === 401) {
                    this.handleUnauthorized();
                }

                return Promise.reject(error);
            }
        );
    }

    // Handle unauthorized access
    handleUnauthorized() {
        console.log('Unauthorized access detected');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        delete axios.defaults.headers.common['Authorization'];

        // Redirect to login after a delay
        setTimeout(() => {
            if (confirm('Phiên đăng nhập đã hết hạn. Bạn có muốn đăng nhập lại?')) {
                window.location.href = '/auth/login';
            }
        }, 1000);
    }

    // Get users with filters, search and pagination
    async getUsers(params = {}) {
        try {
            const queryParams = new URLSearchParams();

            // Add pagination params
            queryParams.append('page', params.page || 0);
            queryParams.append('size', params.size || 10);
            queryParams.append('sortBy', params.sortBy || 'createdAt');
            queryParams.append('sortDir', params.sortDir || 'desc');

            // Add filter params if provided
            if (params.role) queryParams.append('role', params.role);
            if (params.status) queryParams.append('status', params.status);
            if (params.search) queryParams.append('search', params.search);

            const response = await axios.get(`${this.baseURL}?${queryParams.toString()}`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể tải danh sách người dùng');
        }
    }

    // Get user by ID
    async getUserById(userId) {
        try {
            const response = await axios.get(`${this.baseURL}/${userId}`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể tải thông tin người dùng');
        }
    }

    // Update user status (ACTIVE/BLOCKED)
    async updateUserStatus(userId, status) {
        try {
            const response = await axios.put(`${this.baseURL}/${userId}/status`, {
                status: status
            });
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể cập nhật trạng thái người dùng');
        }
    }

    // Delete user (soft delete)
    async deleteUser(userId) {
        try {
            const response = await axios.delete(`${this.baseURL}/${userId}`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể xóa người dùng');
        }
    }

    // Bulk delete users
    async bulkDeleteUsers(userIds) {
        try {
            const response = await axios.delete(`${this.baseURL}/bulk`, {
                data: { userIds: userIds }
            });
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể xóa nhiều người dùng');
        }
    }

    // Get user statistics
    async getUserStats() {
        try {
            const response = await axios.get(`${this.baseURL}/stats`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể tải thống kê người dùng');
        }
    }

    // Search users
    async searchUsers(keyword, page = 0, size = 10) {
        try {
            const params = new URLSearchParams({
                keyword: keyword,
                page: page,
                size: size
            });

            const response = await axios.get(`${this.baseURL}/search?${params.toString()}`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể tìm kiếm người dùng');
        }
    }

    // Get top users by orders
    async getTopUsersByOrders(page = 0, size = 10) {
        try {
            const params = new URLSearchParams({
                page: page,
                size: size
            });

            const response = await axios.get(`${this.baseURL}/top-buyers?${params.toString()}`);
            return this.handleResponse(response);
        } catch (error) {
            return this.handleError(error, 'Không thể tải top khách hàng');
        }
    }

    // Export users to Excel
    async exportUsers(filters = {}) {
        try {
            const queryParams = new URLSearchParams();

            // Add filter params if provided
            if (filters.role) queryParams.append('role', filters.role);
            if (filters.status) queryParams.append('status', filters.status);
            if (filters.search) queryParams.append('search', filters.search);

            const response = await axios.get(`${this.baseURL}/export?${queryParams.toString()}`, {
                responseType: 'blob'
            });

            // Create download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `users_${new Date().toISOString().split('T')[0]}.xlsx`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            return { success: true, message: 'Xuất file thành công' };
        } catch (error) {
            return this.handleError(error, 'Không thể xuất file Excel');
        }
    }

    // Handle successful response
    handleResponse(response) {
        if (response.data && response.data.success) {
            return {
                success: true,
                data: response.data.data,
                message: response.data.message
            };
        } else {
            throw new Error(response.data?.message || 'Phản hồi không hợp lệ từ server');
        }
    }

    // Handle error response
    handleError(error, defaultMessage) {
        console.error('API Error:', error);

        let message = defaultMessage;

        if (error.response) {
            // Server responded with error status
            if (error.response.data?.message) {
                message = error.response.data.message;
            } else if (error.response.status === 403) {
                message = 'Bạn không có quyền thực hiện thao tác này';
            } else if (error.response.status === 404) {
                message = 'Không tìm thấy dữ liệu';
            } else if (error.response.status >= 500) {
                message = 'Lỗi server. Vui lòng thử lại sau';
            }
        } else if (error.request) {
            // Request was made but no response
            message = 'Không thể kết nối đến server';
        }

        return {
            success: false,
            message: message,
            error: error
        };
    }

    // Utility method to show success message
    showSuccessMessage(message) {
        if (typeof window.showToast === 'function') {
            window.showToast(message, 'success');
        } else {
            alert(message);
        }
    }

    // Utility method to show error message
    showErrorMessage(message) {
        if (typeof window.showToast === 'function') {
            window.showToast(message, 'error');
        } else {
            alert(message);
        }
    }
}

// Create global instance
window.AdminUserAPI = new AdminUserAPI();

// Export for use in modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdminUserAPI;
}

/**
 * Enhanced User Manager with actual API integration
 */
class EnhancedUserManager {
    constructor() {
        this.api = window.AdminUserAPI;
        this.currentPage = 0;
        this.pageSize = 10;
        this.totalPages = 0;
        this.selectedUsers = new Set();
        this.currentFilters = {};
    }

    // Load users with actual API call
    async loadUsers(page = 0) {
        try {
            this.currentPage = page;

            // Show loading
            this.showLoading();

            // Get filter values
            this.currentFilters = {
                role: document.getElementById('roleFilter')?.value || '',
                status: document.getElementById('statusFilter')?.value || '',
                search: document.getElementById('searchInput')?.value || '',
                page: page,
                size: this.pageSize,
                sortBy: 'createdAt',
                sortDir: 'desc'
            };

            console.log('Loading users with filters:', this.currentFilters);

            // Make API call
            const response = await this.api.getUsers(this.currentFilters);

            if (response.success) {
                this.renderUsers(response.data);
                this.renderPagination(response.data);
                this.hideLoading();
            } else {
                this.showEmptyState();
                this.api.showErrorMessage(response.message);
            }

        } catch (error) {
            console.error('Error loading users:', error);
            this.showEmptyState();
            this.api.showErrorMessage('Có lỗi xảy ra khi tải danh sách người dùng');
        }
    }

    // View user detail - redirect to detail page
    async viewUserDetail(userId) {
        console.log('Redirecting to user detail page for ID:', userId);
        window.location.href = `/admin/users/detail?id=${userId}`;
    }

    // Toggle user status with API call
    async toggleUserStatus(userId, currentStatus) {
        try {
            const newStatus = currentStatus === 'ACTIVE' ? 'BLOCKED' : 'ACTIVE';
            const action = newStatus === 'ACTIVE' ? 'kích hoạt' : 'khóa';

            if (!confirm(`Bạn có chắc chắn muốn ${action} người dùng này?`)) {
                return;
            }

            console.log(`Toggling user ${userId} status to ${newStatus}`);

            const response = await this.api.updateUserStatus(userId, newStatus);

            if (response.success) {
                this.api.showSuccessMessage(response.message);
                // Reload current page
                this.loadUsers(this.currentPage);
            } else {
                this.api.showErrorMessage(response.message);
            }

        } catch (error) {
            console.error('Error toggling user status:', error);
            this.api.showErrorMessage('Không thể cập nhật trạng thái người dùng');
        }
    }

    // Delete user with API call
    async deleteUser(userId) {
        try {
            if (!confirm('Bạn có chắc chắn muốn xóa người dùng này? Hành động này không thể hoàn tác.')) {
                return;
            }

            console.log('Deleting user:', userId);

            const response = await this.api.deleteUser(userId);

            if (response.success) {
                this.api.showSuccessMessage(response.message);
                // Reload current page
                this.loadUsers(this.currentPage);
            } else {
                this.api.showErrorMessage(response.message);
            }

        } catch (error) {
            console.error('Error deleting user:', error);
            this.api.showErrorMessage('Không thể xóa người dùng');
        }
    }

    // Bulk delete users with API call
    async bulkDeleteUsers() {
        try {
            if (this.selectedUsers.size === 0) {
                this.api.showErrorMessage('Vui lòng chọn ít nhất một người dùng để xóa');
                return;
            }

            if (!confirm(`Bạn có chắc chắn muốn xóa ${this.selectedUsers.size} người dùng đã chọn?`)) {
                return;
            }

            const userIds = Array.from(this.selectedUsers);
            console.log('Bulk deleting users:', userIds);

            const response = await this.api.bulkDeleteUsers(userIds);

            if (response.success) {
                this.api.showSuccessMessage(response.message);
                // Clear selections and reload
                this.selectedUsers.clear();
                this.updateBulkDeleteButton();
                this.loadUsers(this.currentPage);
            } else {
                this.api.showErrorMessage(response.message);
            }

        } catch (error) {
            console.error('Error bulk deleting users:', error);
            this.api.showErrorMessage('Không thể xóa nhiều người dùng');
        }
    }

    // Show user stats - redirect to stats page
    async showUserStats() {
        console.log('Redirecting to user stats page');
        window.location.href = '/admin/users/stats';
    }

    // Export users with API call
    async exportUsers() {
        try {
            console.log('Exporting users with filters:', this.currentFilters);

            const response = await this.api.exportUsers(this.currentFilters);

            if (response.success) {
                this.api.showSuccessMessage(response.message);
            } else {
                this.api.showErrorMessage(response.message);
            }

        } catch (error) {
            console.error('Error exporting users:', error);
            this.api.showErrorMessage('Không thể xuất file Excel');
        }
    }

    // UI Helper methods
    showLoading() {
        document.getElementById('loadingIndicator').style.display = 'block';
        document.getElementById('usersTableContainer').style.display = 'none';
        document.getElementById('emptyState').style.display = 'none';
    }

    hideLoading() {
        document.getElementById('loadingIndicator').style.display = 'none';
    }

    showEmptyState() {
        document.getElementById('usersTableContainer').style.display = 'none';
        document.getElementById('emptyState').style.display = 'block';
        this.hideLoading();
    }

    // Render users table
    renderUsers(data) {
        const tbody = document.getElementById('usersTableBody');
        if (!tbody) return;

        if (data.content && data.content.length > 0) {
            tbody.innerHTML = data.content.map(user => `
                <tr>
                    <td>
                        <input type="checkbox" class="form-check-input user-checkbox"
                               value="${user.id}" onchange="UserManager.toggleUserSelection(${user.id})">
                    </td>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.fullName || '-'}</td>
                    <td>${user.phone || '-'}</td>
                    <td><span class="badge bg-${user.role === 'ADMIN' ? 'danger' : 'primary'}">${user.role}</span></td>
                    <td><span class="badge bg-${user.status === 'ACTIVE' ? 'success' : 'warning'}">${user.status}</span></td>
                    <td>${new Date(user.createdAt).toLocaleDateString('vi-VN')}</td>
                    <td>
                        <button class="btn btn-info btn-sm" onclick="UserManager.viewUserDetail(${user.id})"
                                title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-${user.status === 'ACTIVE' ? 'warning' : 'success'} btn-sm"
                                onclick="UserManager.toggleUserStatus(${user.id}, '${user.status}')"
                                title="${user.status === 'ACTIVE' ? 'Khóa' : 'Kích hoạt'}">
                            <i class="fas fa-${user.status === 'ACTIVE' ? 'lock' : 'unlock'}"></i>
                        </button>
                        ${user.role !== 'ADMIN' ? `
                            <button class="btn btn-danger btn-sm" onclick="UserManager.deleteUser(${user.id})"
                                    title="Xóa">
                                <i class="fas fa-trash"></i>
                            </button>
                        ` : ''}
                    </td>
                </tr>
            `).join('');

            document.getElementById('usersTableContainer').style.display = 'block';
        } else {
            this.showEmptyState();
        }
    }

    // Render pagination
    renderPagination(data) {
        const container = document.getElementById('paginationContainer');
        if (!container) return;

        this.totalPages = data.totalPages;

        if (data.totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let paginationHtml = '<ul class="pagination justify-content-center">';

        // Previous button
        paginationHtml += `
            <li class="page-item ${data.first ? 'disabled' : ''}">
                <button class="page-link" onclick="UserManager.loadUsers(${data.page - 1})"
                        ${data.first ? 'disabled' : ''}>
                    <i class="fas fa-chevron-left"></i>
                </button>
            </li>
        `;

        // Page numbers
        for (let i = Math.max(0, data.page - 2); i <= Math.min(data.totalPages - 1, data.page + 2); i++) {
            paginationHtml += `
                <li class="page-item ${i === data.page ? 'active' : ''}">
                    <button class="page-link" onclick="UserManager.loadUsers(${i})">${i + 1}</button>
                </li>
            `;
        }

        // Next button
        paginationHtml += `
            <li class="page-item ${data.last ? 'disabled' : ''}">
                <button class="page-link" onclick="UserManager.loadUsers(${data.page + 1})"
                        ${data.last ? 'disabled' : ''}>
                    <i class="fas fa-chevron-right"></i>
                </button>
            </li>
        `;

        paginationHtml += '</ul>';
        container.innerHTML = paginationHtml;
    }

    // Toggle select all users
    toggleSelectAll() {
        const selectAll = document.getElementById('selectAllUsers');
        const userCheckboxes = document.querySelectorAll('.user-checkbox');

        this.selectedUsers.clear();

        userCheckboxes.forEach(checkbox => {
            checkbox.checked = selectAll.checked;
            if (selectAll.checked) {
                this.selectedUsers.add(parseInt(checkbox.value));
            }
        });

        this.updateBulkDeleteButton();
    }

    // Toggle user selection
    toggleUserSelection(userId) {
        if (this.selectedUsers.has(userId)) {
            this.selectedUsers.delete(userId);
        } else {
            this.selectedUsers.add(userId);
        }
        this.updateBulkDeleteButton();
        this.updateSelectAllCheckbox();
    }

    // Update bulk delete button visibility
    updateBulkDeleteButton() {
        const bulkDeleteBtn = document.getElementById('bulkDeleteBtn');
        if (bulkDeleteBtn) {
            bulkDeleteBtn.style.display = this.selectedUsers.size > 0 ? 'inline-block' : 'none';
        }
    }

    // Update select all checkbox state
    updateSelectAllCheckbox() {
        const selectAll = document.getElementById('selectAllUsers');
        const userCheckboxes = document.querySelectorAll('.user-checkbox');

        if (selectAll && userCheckboxes.length > 0) {
            const checkedCount = Array.from(userCheckboxes).filter(cb => cb.checked).length;
            selectAll.checked = checkedCount === userCheckboxes.length;
            selectAll.indeterminate = checkedCount > 0 && checkedCount < userCheckboxes.length;
        }
    }
}

// Replace the original UserManager with enhanced version when API is ready
window.EnhancedUserManager = EnhancedUserManager;