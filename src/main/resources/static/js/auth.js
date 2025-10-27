/**
 * Authentication utilities
 * Updated version: Session-based UI, localStorage token preserved for compatibility
 */

const Auth = {
    // Get token from localStorage (kept for compatibility with other features)
    getToken() {
        return localStorage.getItem('token');
    },

    // Get user info from localStorage
    getUser() {
        const userStr = localStorage.getItem('user');
        try {
            return userStr ? JSON.parse(userStr) : null;
        } catch (e) {
            console.error('Error parsing user data:', e);
            this.clearAuth();
            return null;
        }
    },

    // Set token and user info (kept for compatibility)
    setAuth(token, user) {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
        this.setupAxiosAuth(token);
    },

    // Clear authentication data
    clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('userData');

        // Clear all auth-related items
        Object.keys(localStorage).forEach(key => {
            if (key.includes('user') || key.includes('token') || key.includes('auth')) {
                localStorage.removeItem(key);
            }
        });

        if (typeof axios !== 'undefined') {
            delete axios.defaults.headers.common['Authorization'];
        }
    },

    // Setup axios with authorization header (kept for compatibility)
    setupAxiosAuth(token) {
        if (token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }
    },

    // Check if user is logged in (localStorage check - kept for compatibility)
    isAuthenticated() {
        const token = this.getToken();
        const user = this.getUser();
        return !!(token && user);
    },

    // Check session-based authentication (PRIMARY METHOD)
    async isSessionAuthenticated() {
        try {
            const response = await fetch('/api/auth/session-info');
            const result = await response.json();
            return response.ok && result.success && result.data.userInfo;
        } catch (error) {
            console.error('Error checking session:', error);
            return false;
        }
    },

    // Check if user is admin
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ADMIN';
    },

    // Check if user is regular user
    isUser() {
        const user = this.getUser();
        return user && user.role === 'USER';
    },

    // Logout function - delegates to AuthUI if available
    async logout() {
        if (window.AuthUI && window.AuthUI.handleLogout) {
            // Use AuthUI logout if available
            await window.AuthUI.handleLogout({ preventDefault: () => {} });
        } else {
            // Fallback logout
            try {
                console.log('Auth: Fallback logout...');
                this.clearAuth();

                if (typeof axios !== 'undefined') {
                    await axios.post('/api/auth/logout');
                }

                if (typeof sessionStorage !== 'undefined') {
                    sessionStorage.clear();
                }

                window.location.replace('/auth/login?t=' + Date.now());
            } catch (error) {
                console.error('Auth: Logout error:', error);
                this.clearAuth();
                window.location.replace('/auth/login?t=' + Date.now());
            }
        }
    },

    // Redirect based on role
    redirectBasedOnRole() {
        if (!this.isAuthenticated()) {
            window.location.href = '/auth/login';
            return;
        }

        if (this.isAdmin()) {
            window.location.href = '/admin/dashboard';
        } else {
            window.location.href = '/user/home';
        }
    },

    // Check authentication status (kept disabled as per your request)
    async checkAuth() {
        const currentPath = window.location.pathname;

        // Skip auth check for auth pages
        if (currentPath.startsWith('/auth/')) {
            return true;
        }

        // AUTH CHECK DISABLED FOR DEBUGGING
        console.log('Auth check disabled for path:', currentPath);
        return true;
    },

    // Initialize axios interceptors (kept for compatibility)
    initAxiosInterceptors() {
        if (typeof axios === 'undefined') return;

        // Request interceptor
        axios.interceptors.request.use(
            (config) => {
                const token = this.getToken();
                if (token) {
                    config.headers['Authorization'] = `Bearer ${token}`;
                }
                return config;
            },
            (error) => {
                return Promise.reject(error);
            }
        );

        // Response interceptor
        axios.interceptors.response.use(
            (response) => {
                return response;
            },
            (error) => {
                if (error.response && error.response.status === 401) {
                    this.clearAuth();
                    if (!window.location.pathname.startsWith('/auth/')) {
                        window.location.href = '/auth/login';
                    }
                }
                return Promise.reject(error);
            }
        );
    },

    // Initialize auth system
    async init() {
        console.log('Auth: Initializing...');

        // Setup axios auth header if token exists
        const token = this.getToken();
        if (token) {
            this.setupAxiosAuth(token);
        }

        // Initialize axios interceptors
        this.initAxiosInterceptors();

        // Check authentication status
        await this.checkAuth();

        // UI update is now handled by AuthUI module
        // Just initialize AuthUI if it's available
        if (window.AuthUI && !window.AuthUI._initialized) {
            await window.AuthUI.init();
            window.AuthUI._initialized = true;
        }

        console.log('Auth: Initialized');
    },

    // Update UI - delegates to AuthUI module
    async updateUserUI() {
        console.log('Auth: updateUserUI called, delegating to AuthUI...');
        if (window.AuthUI) {
            await window.AuthUI.refresh();
        } else {
            console.warn('Auth: AuthUI module not loaded yet');
        }
    },

    // Show login required modal - delegates to AuthUI
    showLoginRequiredModal() {
        if (window.AuthUI) {
            window.AuthUI.showLoginRequiredModal();
        } else {
            alert('Bạn cần đăng nhập để truy cập tính năng này.');
            window.location.href = '/auth/login';
        }
    },

    // Setup restricted link handlers - delegates to AuthUI
    setupRestrictedLinkHandlers() {
        if (window.AuthUI) {
            window.AuthUI.setupRestrictedLinks();
        }
    },

    // Legacy logout handlers - delegates to AuthUI
    setupLogoutHandlers() {
        if (window.AuthUI) {
            window.AuthUI.setupEventHandlers();
        }
    },

    // Legacy fallback username
    setFallbackUsername() {
        console.log('Auth: Setting fallback username...');
        const usernameElements = document.querySelectorAll('#username, #adminUsername, #userFullName');
        usernameElements.forEach(el => {
            if (el) {
                el.textContent = 'Khách hàng';
            }
        });
    }
};

// Initialize auth system when DOM is loaded
document.addEventListener('DOMContentLoaded', async function () {
    console.log('auth.js: DOM loaded, initializing auth system...');
    await Auth.init();
    console.log('auth.js: Auth system initialized');
});

// Export Auth object for use in other scripts
window.Auth = Auth;

// Global logout function for easy access
window.logout = function () {
    console.log('Global logout function called');
    if (window.Auth) {
        if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
            console.log('User confirmed logout');
            window.Auth.logout();
        } else {
            console.log('User cancelled logout');
        }
    } else {
        console.error('Auth object not available');
        localStorage.clear();
        window.location.href = '/auth/login';
    }
};

// Simple test logout function
window.testLogout = function () {
    console.log('Test logout called');
    localStorage.clear();
    window.location.href = '/auth/login';
};

// Skeleton Loading Management
function hideSkeleton() {
    const skeletonLoader = document.getElementById('skeletonLoader');
    if (skeletonLoader) {
        skeletonLoader.classList.add('skeleton-hidden');
    }
}

function showSkeleton() {
    const skeletonLoader = document.getElementById('skeletonLoader');
    if (skeletonLoader) {
        skeletonLoader.classList.remove('skeleton-hidden');
    }
}

// Make functions globally available
window.hideSkeleton = hideSkeleton;
window.showSkeleton = showSkeleton;

// Initialize user layout dropdown functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing user layout...');

    // Set active navigation based on current URL
    setActiveNavigation();

    console.log('User layout initialized successfully');
});

function setActiveNavigation() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

    // Remove active class from all nav links
    navLinks.forEach(link => {
        link.classList.remove('active');
    });

    // Add active class to current page nav link
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath === href) {
            link.classList.add('active');
        } else if (href && currentPath.startsWith(href) && href !== '/user/home') {
            link.classList.add('active');
        }
    });

    // Special case for home page
    if (currentPath === '/user/home' || currentPath === '/') {
        const homeLink = document.querySelector('.navbar-brand');
        if (homeLink) {
            homeLink.style.opacity = '1';
        }
    }
}