package com.electronic.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {

    /**
     * Root path - redirect to login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/user/home";
    }

    /**
     * Auth Routes - Only keeping login and register
     */
    @GetMapping("/auth/login")
    public String login(Model model) {
        model.addAttribute("title", "Đăng Nhập - Electronic Store");
        return "auth/login";
    }

    @PostMapping("/auth/login")
    public String loginPost() {
        // Redirect to prevent form submission - JavaScript will handle the login
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/register")
    public String register(Model model) {
        model.addAttribute("title", "Đăng Ký - Electronic Store");
        return "auth/register";
    }

    @GetMapping("/auth/check-email")
    public String checkEmail(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("title", "Kiểm Tra Email - Electronic Store");
        model.addAttribute("email", email);
        return "auth/check-email";
    }

    @GetMapping("/auth/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {
        // Call AuthService directly instead of redirecting to API
        return "redirect:/api/auth/verify-email?token=" + token;
    }

    @GetMapping("/auth/email-verification-success")
    public String emailVerificationSuccess(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("title", "Email Xác Thực Thành Công - Electronic Store");
        if (message == null) {
            message = "Email đã được xác thực thành công! Bạn có thể đăng nhập ngay bây giờ.";
        }
        model.addAttribute("message", message);
        return "auth/email-verification-success";
    }

    /**
     * Admin Routes
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Dashboard - Admin Panel");
        return "admin/dashboard";
    }

    @GetMapping("/admin/layouts/app.html")
    public String adminLayout() {
        return "admin/layouts/app";
    }

    @GetMapping("/admin/layout")
    @ResponseBody
    public String adminLayoutShort() throws Exception {
        // Đọc file layout trực tiếp và trả về raw HTML
        try {
            java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/admin/layouts/app.html");
            if (inputStream == null) {
                return "<div class='alert alert-danger'>Layout file not found</div>";
            }

            java.util.Scanner scanner = new java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            inputStream.close();

            return content;
        } catch (Exception e) {
            return "<div class='alert alert-danger'>Error reading layout: " + e.getMessage() + "</div>";
        }
    }

    @GetMapping("/admin/categories")
    public String adminCategories(Model model) {
        model.addAttribute("title", "Quản lý Danh mục");
        return "admin/category/index";
    }

    @GetMapping("/admin/categories/create")
    public String adminCategoriesCreate(Model model) {
        model.addAttribute("title", "Thêm Danh mục mới");
        return "admin/category/create";
    }

    @GetMapping("/admin/categories/{id}/edit")
    public String adminCategoriesEdit(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh sửa Danh mục");
        model.addAttribute("categoryId", id);
        return "admin/category/edit";
    }

    @GetMapping("/admin/categories/test")
    public String adminCategoriesTest(Model model) {
        model.addAttribute("title", "Test Categories");
        return "admin/category/test";
    }

    /**
     * Product Routes
     */
    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        model.addAttribute("title", "Quản lý Sản phẩm");
        return "admin/product/index";
    }

    @GetMapping("/admin/products/create")
    public String adminProductsCreate(Model model) {
        model.addAttribute("title", "Thêm Sản phẩm mới");
        return "admin/product/create";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String adminProductsEdit(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh sửa Sản phẩm");
        model.addAttribute("productId", id);
        return "admin/product/edit";
    }

    /**
     * Promotion Routes
     */
    @GetMapping("/admin/promotions")
    public String adminPromotions(Model model) {
        model.addAttribute("title", "Quản lý Khuyến mãi");
        return "admin/promotion/index";
    }

    @GetMapping("/admin/promotions/create")
    public String adminPromotionsCreate(Model model) {
        model.addAttribute("title", "Thêm Khuyến mãi mới");
        return "admin/promotion/create";
    }

    @GetMapping("/admin/promotions/edit")
    public String adminPromotionsEdit(Model model) {
        model.addAttribute("title", "Chỉnh sửa Khuyến mãi");
        return "admin/promotion/edit";
    }

    /**
     * Admin User Management Routes
     */
    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("title", "Quản lý Người dùng");
        return "admin/users/index";
    }

    @GetMapping("/admin/users/detail")
    public String adminUserDetail(Model model) {
        model.addAttribute("title", "Chi tiết Người dùng");
        return "admin/users/detail";
    }

    @GetMapping("/admin/users/stats")
    public String adminUserStats(Model model) {
        model.addAttribute("title", "Thống kê Người dùng");
        return "admin/users/stats";
    }


    /**
     * User Routes
     */
    @GetMapping("/user/home")
    public String userHome(Model model) {
        model.addAttribute("title", "Trang chủ - Electronic Store");
        return "user/home";
    }

    @GetMapping("/user/layouts/app.html")
    public String userLayout() {
        return "user/layouts/app";
    }

    @GetMapping("/user/layout")
    public String userLayoutShort() {
        return "user/layouts/app";
    }

    @GetMapping("/user/products")
    public String userProducts(Model model) {
        model.addAttribute("title", "Sản phẩm - Electronic Store");
        return "user/product/index";
    }

    @GetMapping("/user/products/{id}")
    public String userProductDetail(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi tiết sản phẩm - Electronic Store");
        model.addAttribute("productId", id);
        return "user/product/detail";
    }

    @GetMapping("/user/cart")
    public String userCart(Model model) {
        model.addAttribute("title", "Giỏ hàng - Electronic Store");
        return "user/cart/index";
    }

    @GetMapping("/user/checkout")
    public String userCheckout(Model model) {
        model.addAttribute("title", "Thanh toán - Electronic Store");
        return "user/checkout/index";
    }

    @GetMapping("/user/payment/result")
    public String userPaymentResult(Model model) {
        model.addAttribute("title", "Kết quả thanh toán - Electronic Store");
        return "user/payment/result";
    }

    @GetMapping("/user/orders")
    public String userOrders(Model model) {
        model.addAttribute("title", "Lịch sử đặt hàng - Electronic Store");
        return "user/orders/index";
    }

    @GetMapping("/user/wishlist")
    public String userWishlist(Model model) {
        model.addAttribute("title", "Danh sách yêu thích - Electronic Store");
        return "user/wishlist/index";
    }

    @GetMapping("/user/faq")
    public String userFaq(Model model) {
        model.addAttribute("title", "Câu hỏi thường gặp - Electronic Store");
        return "user/faq/index";
    }

    @GetMapping("/user/contact")
    public String userContact(Model model) {
        model.addAttribute("title", "Liên hệ -5 Electronic Store");
        return "user/contact/index";
    }
}