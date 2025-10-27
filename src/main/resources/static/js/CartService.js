/**
 * CartService - Quản lý giỏ hàng với localStorage
 * Frontend-only implementation
 */
class CartService {
    constructor() {
        this.storageKey = 'electronic_store_cart';
        this.cart = this.loadCart();
        this.listeners = [];
    }

    /**
     * Load cart từ localStorage
     */
    loadCart() {
        try {
            const cartData = localStorage.getItem(this.storageKey);
            if (cartData) {
                const cart = JSON.parse(cartData);
                // Validate cart structure
                if (cart && cart.items && Array.isArray(cart.items)) {
                    return cart;
                }
            }
        } catch (error) {
            console.error('Error loading cart from localStorage:', error);
        }

        // Return empty cart if no valid data found
        return {
            items: [],
            totalItems: 0,
            totalAmount: 0,
            lastUpdated: new Date().toISOString()
        };
    }

    /**
     * Save cart to localStorage
     */
    saveCart() {
        try {
            this.cart.lastUpdated = new Date().toISOString();
            localStorage.setItem(this.storageKey, JSON.stringify(this.cart));
            this.notifyListeners();
        } catch (error) {
            console.error('Error saving cart to localStorage:', error);
        }
    }

    /**
     * Thêm listener để theo dõi thay đổi cart
     */
    addListener(callback) {
        this.listeners.push(callback);
    }

    /**
     * Remove listener
     */
    removeListener(callback) {
        this.listeners = this.listeners.filter(listener => listener !== callback);
    }

    /**
     * Notify tất cả listeners khi cart thay đổi
     */
    notifyListeners() {
        this.listeners.forEach(callback => {
            try {
                callback(this.cart);
            } catch (error) {
                console.error('Error in cart listener:', error);
            }
        });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    addItem(product, quantity = 1) {
        try {
            // Validate input
            if (!product || !product.id || !product.name || !product.price) {
                throw new Error('Invalid product data');
            }

            if (quantity <= 0) {
                throw new Error('Quantity must be greater than 0');
            }

            if (product.stock !== undefined && quantity > product.stock) {
                throw new Error(`Chỉ còn ${product.stock} sản phẩm trong kho`);
            }

            // Check if item already exists
            const existingItemIndex = this.cart.items.findIndex(item => item.productId === product.id);

            if (existingItemIndex >= 0) {
                // Update existing item
                const existingItem = this.cart.items[existingItemIndex];
                const newQuantity = existingItem.quantity + quantity;

                if (product.stock !== undefined && newQuantity > product.stock) {
                    throw new Error(`Chỉ có thể thêm tối đa ${product.stock - existingItem.quantity} sản phẩm nữa`);
                }

                existingItem.quantity = newQuantity;
                existingItem.updatedAt = new Date().toISOString();
            } else {
                // Add new item
                const cartItem = {
                    productId: product.id,
                    name: product.name,
                    price: product.price,
                    quantity: quantity,
                    imageUrl: product.imageUrl || null,
                    stock: product.stock || 0,
                    addedAt: new Date().toISOString(),
                    updatedAt: new Date().toISOString()
                };

                this.cart.items.push(cartItem);
            }

            this.updateCartTotals();
            this.saveCart();

            return {
                success: true,
                message: `Đã thêm ${quantity} sản phẩm vào giỏ hàng`,
                cart: this.cart
            };

        } catch (error) {
            return {
                success: false,
                message: error.message,
                cart: this.cart
            };
        }
    }

    /**
     * Cập nhật số lượng sản phẩm
     */
    updateItem(productId, quantity) {
        try {
            if (quantity <= 0) {
                return this.removeItem(productId);
            }

            const itemIndex = this.cart.items.findIndex(item => item.productId === productId);
            if (itemIndex === -1) {
                throw new Error('Sản phẩm không có trong giỏ hàng');
            }

            const item = this.cart.items[itemIndex];
            if (item.stock !== undefined && quantity > item.stock) {
                throw new Error(`Chỉ còn ${item.stock} sản phẩm trong kho`);
            }

            item.quantity = quantity;
            item.updatedAt = new Date().toISOString();

            this.updateCartTotals();
            this.saveCart();

            return {
                success: true,
                message: 'Đã cập nhật số lượng sản phẩm',
                cart: this.cart
            };

        } catch (error) {
            return {
                success: false,
                message: error.message,
                cart: this.cart
            };
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    removeItem(productId) {
        try {
            const itemIndex = this.cart.items.findIndex(item => item.productId === productId);
            if (itemIndex === -1) {
                throw new Error('Sản phẩm không có trong giỏ hàng');
            }

            const removedItem = this.cart.items.splice(itemIndex, 1)[0];

            this.updateCartTotals();
            this.saveCart();

            return {
                success: true,
                message: `Đã xóa "${removedItem.name}" khỏi giỏ hàng`,
                cart: this.cart
            };

        } catch (error) {
            return {
                success: false,
                message: error.message,
                cart: this.cart
            };
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    clearCart() {
        this.cart = {
            items: [],
            totalItems: 0,
            totalAmount: 0,
            lastUpdated: new Date().toISOString()
        };

        this.saveCart();

        return {
            success: true,
            message: 'Đã xóa toàn bộ giỏ hàng',
            cart: this.cart
        };
    }

    /**
     * Lấy thông tin giỏ hàng
     */
    getCart() {
        return { ...this.cart };
    }

    /**
     * Lấy số lượng items trong giỏ hàng
     */
    getItemCount() {
        return this.cart.totalItems;
    }

    /**
     * Lấy tổng tiền
     */
    getTotalAmount() {
        return this.cart.totalAmount;
    }

    /**
     * Kiểm tra sản phẩm có trong giỏ hàng không
     */
    hasItem(productId) {
        return this.cart.items.some(item => item.productId === productId);
    }

    /**
     * Lấy thông tin một item trong giỏ hàng
     */
    getItem(productId) {
        return this.cart.items.find(item => item.productId === productId) || null;
    }

    /**
     * Cập nhật tổng số lượng và tổng tiền
     */
    updateCartTotals() {
        this.cart.totalItems = this.cart.items.reduce((total, item) => total + item.quantity, 0);
        this.cart.totalAmount = this.cart.items.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    /**
     * Validate stock cho tất cả items (dùng khi check out)
     */
    async validateStock() {
        const validationResults = [];

        for (const item of this.cart.items) {
            try {
                // Call API to check current stock
                const response = await axios.get(`/api/products/${item.productId}`);
                if (response.data.success) {
                    const currentStock = response.data.data.stock;
                    if (item.quantity > currentStock) {
                        validationResults.push({
                            productId: item.productId,
                            name: item.name,
                            requested: item.quantity,
                            available: currentStock,
                            valid: false
                        });
                    } else {
                        validationResults.push({
                            productId: item.productId,
                            name: item.name,
                            requested: item.quantity,
                            available: currentStock,
                            valid: true
                        });
                    }
                }
            } catch (error) {
                validationResults.push({
                    productId: item.productId,
                    name: item.name,
                    requested: item.quantity,
                    available: 0,
                    valid: false,
                    error: 'Không thể kiểm tra tồn kho'
                });
            }
        }

        return validationResults;
    }
}

// Create global instance
window.cartService = new CartService();

// Export for modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CartService;
}