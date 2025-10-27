// Wishlist Service - Global wishlist functionality
class WishlistService {
    constructor() {
        this.baseUrl = '/api/wishlist';
        this.init();
    }

    async init() {
        await this.updateCounter();
    }

    // Get user's wishlist
    async getWishlist() {
        try {
            const response = await axios.get(this.baseUrl);
            return response.data;
        } catch (error) {
            console.error('Error getting wishlist:', error);
            throw error;
        }
    }

    // Add product to wishlist
    async addToWishlist(productId) {
        try {
            const response = await axios.post(`${this.baseUrl}/${productId}`);
            await this.updateCounter();
            return response.data;
        } catch (error) {
            console.error('Error adding to wishlist:', error);
            throw error;
        }
    }

    // Remove product from wishlist
    async removeFromWishlist(productId) {
        try {
            const response = await axios.delete(`${this.baseUrl}/${productId}`);
            await this.updateCounter();
            return response.data;
        } catch (error) {
            console.error('Error removing from wishlist:', error);
            throw error;
        }
    }

    // Check if product is in wishlist
    async isInWishlist(productId) {
        try {
            const response = await axios.get(`${this.baseUrl}/check/${productId}`);
            return response.data;
        } catch (error) {
            console.error('Error checking wishlist:', error);
            return { success: false, data: false };
        }
    }

    // Get wishlist count
    async getWishlistCount() {
        try {
            const response = await axios.get(`${this.baseUrl}/count`);
            return response.data;
        } catch (error) {
            console.error('Error getting wishlist count:', error);
            return { success: false, data: 0 };
        }
    }

    // Clear entire wishlist
    async clearWishlist() {
        try {
            const response = await axios.delete(`${this.baseUrl}/clear`);
            await this.updateCounter();
            return response.data;
        } catch (error) {
            console.error('Error clearing wishlist:', error);
            throw error;
        }
    }

    // Update wishlist counter in navigation
    async updateCounter() {
        try {
            const result = await this.getWishlistCount();
            if (result.success) {
                const counter = document.getElementById('wishlistCounter');
                if (counter) {
                    const count = result.data;
                    if (count > 0) {
                        counter.textContent = count;
                        counter.style.display = 'inline';
                    } else {
                        counter.style.display = 'none';
                    }
                }
            }
        } catch (error) {
            console.log('Error updating wishlist counter');
        }
    }
}

// Global wishlist service instance
let wishlistService = null;

// Initialize wishlist service
function initWishlistService() {
    if (!wishlistService) {
        wishlistService = new WishlistService();
    }
    return wishlistService;
}

// Global functions for easy access
async function addToWishlistGlobal(productId) {
    try {
        const service = initWishlistService();
        const result = await service.addToWishlist(productId);

        if (result.success) {
            showWishlistNotification('success', result.message);
            return { success: true, message: result.message };
        } else {
            showWishlistNotification('error', result.message);
            return { success: false, message: result.message };
        }
    } catch (error) {
        const message = error.response && error.response.status === 401
            ? 'Vui lòng đăng nhập để sử dụng chức năng này'
            : 'Không thể thêm vào danh sách yêu thích';

        showWishlistNotification('error', message);
        return { success: false, message };
    }
}

async function removeFromWishlistGlobal(productId) {
    try {
        const service = initWishlistService();
        const result = await service.removeFromWishlist(productId);

        if (result.success) {
            showWishlistNotification('success', result.message);
            return { success: true, message: result.message };
        } else {
            showWishlistNotification('error', result.message);
            return { success: false, message: result.message };
        }
    } catch (error) {
        const message = 'Không thể xóa khỏi danh sách yêu thích';
        showWishlistNotification('error', message);
        return { success: false, message };
    }
}

async function checkWishlistStatusGlobal(productId) {
    try {
        const service = initWishlistService();
        const result = await service.isInWishlist(productId);
        return result.success ? result.data : false;
    } catch (error) {
        return false;
    }
}

// Notification function for wishlist actions
function showWishlistNotification(type, message) {
    // Try to use existing cart notification system first
    if (typeof showCartNotification === 'function') {
        showCartNotification(type, message);
        return;
    }

    // Fallback notification system
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show position-fixed`;
    notification.style.cssText = `
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    `;

    const icon = type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle';

    notification.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="${icon} me-2"></i>
            <span>${message}</span>
            <button type="button" class="btn-close ms-auto" data-bs-dismiss="alert"></button>
        </div>
    `;

    document.body.appendChild(notification);

    // Auto remove after 3 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Small delay to ensure other scripts are loaded
    setTimeout(() => {
        initWishlistService();
    }, 100);
});