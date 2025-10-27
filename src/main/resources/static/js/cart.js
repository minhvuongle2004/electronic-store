/**
 * Cart Utility Functions
 * Helper functions for cart operations and UI updates
 */

/**
 * Format giá tiền theo định dạng VND
 */
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

/**
 * Escape HTML để tránh XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

/**
 * Hiển thị toast notification
 */
function showCartNotification(type, message, duration = 3000) {
    // Remove existing notifications
    const existingToasts = document.querySelectorAll('.cart-toast');
    existingToasts.forEach(toast => toast.remove());

    // Create toast HTML
    const toastId = 'cartToast' + Date.now();
    const toastHTML = `
        <div class="toast cart-toast position-fixed" id="${toastId}"
             style="top: 80px; right: 20px; z-index: 9999;" role="alert">
            <div class="toast-header">
                <i class="fas fa-shopping-cart text-primary me-2"></i>
                <strong class="me-auto">Giỏ hàng</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body ${type === 'success' ? 'text-success' : type === 'error' ? 'text-danger' : 'text-info'}">
                ${escapeHtml(message)}
            </div>
        </div>
    `;

    // Add toast to page
    document.body.insertAdjacentHTML('beforeend', toastHTML);

    // Show toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        delay: duration
    });
    toast.show();

    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

/**
 * Cập nhật cart counter trong navigation
 */
function updateCartCounter() {
    const cartCounter = document.getElementById('cartCounter');
    if (cartCounter && window.cartService) {
        const itemCount = window.cartService.getItemCount();

        if (itemCount > 0) {
            cartCounter.textContent = itemCount > 99 ? '99+' : itemCount;
            cartCounter.style.display = 'block';
        } else {
            cartCounter.style.display = 'none';
        }
    }
}

/**
 * Initialize cart functionality
 */
function initializeCart() {
    if (window.cartService) {
        // Add listener để update cart counter khi cart thay đổi
        window.cartService.addListener(function(cart) {
            updateCartCounter();
        });

        // Update counter lần đầu
        updateCartCounter();
    }
}

/**
 * Thêm sản phẩm vào giỏ hàng với UI feedback
 */
function addToCartWithFeedback(product, quantity = 1) {
    if (!window.cartService) {
        console.error('CartService not available');
        showCartNotification('error', 'Lỗi hệ thống giỏ hàng');
        return;
    }

    const result = window.cartService.addItem(product, quantity);

    if (result.success) {
        showCartNotification('success', result.message);

        // Optional: Animate add to cart button
        const addButton = document.querySelector('button[onclick*="addToCart"]');
        if (addButton) {
            addButton.classList.add('btn-success');
            addButton.innerHTML = '<i class="fas fa-check me-2"></i>Đã thêm!';

            setTimeout(() => {
                addButton.classList.remove('btn-success');
                addButton.innerHTML = '<i class="fas fa-shopping-cart me-2"></i>Thêm vào giỏ hàng';
            }, 2000);
        }
    } else {
        showCartNotification('error', result.message);
    }

    return result;
}

/**
 * Render cart summary (for cart page or mini cart)
 */
function renderCartSummary(containerId) {
    const container = document.getElementById(containerId);
    if (!container || !window.cartService) return;

    const cart = window.cartService.getCart();

    if (cart.items.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                <h5 class="text-muted">Giỏ hàng trống</h5>
                <p class="text-muted">Hãy thêm sản phẩm vào giỏ hàng</p>
                <a href="/user/products" class="btn btn-primary">
                    <i class="fas fa-shopping-bag me-2"></i>Mua sắm ngay
                </a>
            </div>
        `;
        return;
    }

    const cartHTML = `
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-shopping-cart me-2"></i>
                            Giỏ hàng của bạn (${cart.totalItems} sản phẩm)
                        </h5>
                    </div>
                    <div class="card-body">
                        ${cart.items.map((item, index) => `
                            <div class="row align-items-center cart-item py-3 ${index > 0 ? 'border-top' : ''}" data-product-id="${item.productId}">
                                <div class="col-md-2">
                                    ${item.imageUrl ?
                                        `<img src="${item.imageUrl}" alt="${escapeHtml(item.name)}" class="img-fluid rounded">` :
                                        `<div class="bg-light rounded d-flex align-items-center justify-content-center" style="height: 80px;">
                                            <i class="fas fa-image text-muted"></i>
                                         </div>`
                                    }
                                </div>
                                <div class="col-md-4">
                                    <h6 class="mb-1">${escapeHtml(item.name)}</h6>
                                    <small class="text-muted">Đơn giá: ${formatPrice(item.price)}</small>
                                </div>
                                <div class="col-md-3">
                                    <div class="d-flex align-items-center">
                                        <button class="btn btn-outline-secondary btn-sm" onclick="updateCartItemQuantity(${item.productId}, ${item.quantity - 1})">
                                            <i class="fas fa-minus"></i>
                                        </button>
                                        <input type="number" class="form-control form-control-sm mx-2 text-center"
                                               style="width: 60px;" value="${item.quantity}" min="1" max="${item.stock}"
                                               onchange="updateCartItemQuantity(${item.productId}, parseInt(this.value))">
                                        <button class="btn btn-outline-secondary btn-sm" onclick="updateCartItemQuantity(${item.productId}, ${item.quantity + 1})">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <strong class="text-primary">${formatPrice(item.price * item.quantity)}</strong>
                                </div>
                                <div class="col-md-1">
                                    <button class="btn btn-outline-danger btn-sm" onclick="removeCartItem(${item.productId})" title="Xóa">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Tổng kết đơn hàng</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <span>${formatPrice(cart.totalAmount)}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển:</span>
                            <span class="text-success">Miễn phí</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-3">
                            <strong>Tổng cộng:</strong>
                            <strong class="text-primary">${formatPrice(cart.totalAmount)}</strong>
                        </div>
                        <div class="d-grid gap-2">
                            <button class="btn btn-primary btn-lg" onclick="proceedToCheckout()">
                                <i class="fas fa-credit-card me-2"></i>Thanh toán
                            </button>
                            <button class="btn btn-outline-danger" onclick="clearCart()">
                                <i class="fas fa-trash me-2"></i>Xóa tất cả
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    container.innerHTML = cartHTML;
}

/**
 * Update cart item quantity
 */
function updateCartItemQuantity(productId, quantity) {
    if (!window.cartService) return;

    const result = window.cartService.updateItem(productId, quantity);

    if (result.success) {
        showCartNotification('success', result.message);
        // Re-render cart if on cart page
        if (document.getElementById('cartContainer')) {
            renderCartSummary('cartContainer');
        }
    } else {
        showCartNotification('error', result.message);
        // Re-render to restore previous state
        if (document.getElementById('cartContainer')) {
            renderCartSummary('cartContainer');
        }
    }
}

/**
 * Remove item from cart
 */
function removeCartItem(productId) {
    if (!window.cartService) return;

    if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        const result = window.cartService.removeItem(productId);

        if (result.success) {
            showCartNotification('success', result.message);
            // Re-render cart if on cart page
            if (document.getElementById('cartContainer')) {
                renderCartSummary('cartContainer');
            }
        } else {
            showCartNotification('error', result.message);
        }
    }
}

/**
 * Clear entire cart
 */
function clearCart() {
    if (!window.cartService) return;

    if (confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) {
        const result = window.cartService.clearCart();

        if (result.success) {
            showCartNotification('success', result.message);
            // Re-render cart if on cart page
            if (document.getElementById('cartContainer')) {
                renderCartSummary('cartContainer');
            }
        } else {
            showCartNotification('error', result.message);
        }
    }
}

/**
 * Proceed to checkout
 */
function proceedToCheckout() {
    if (!window.cartService) return;

    const cart = window.cartService.getCart();
    if (cart.items.length === 0) {
        showCartNotification('error', 'Giỏ hàng trống');
        return;
    }

    // Redirect to checkout page
    window.location.href = '/user/checkout';
}

// Initialize cart when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeCart();
});