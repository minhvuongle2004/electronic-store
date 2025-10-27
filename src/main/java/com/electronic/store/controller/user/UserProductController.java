package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Product;
import com.electronic.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserProductController {

    @Autowired
    private ProductService productService;

    /**
     * Lấy danh sách sản phẩm với phân trang và sắp xếp (cho user)
     * GET /api/products?page=0&sort=price_asc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sort) {
        try {
            Page<Product> products;
            if (sort != null && !sort.isEmpty()) {
                products = productService.getAllProductsForUserWithSort(page, sort);
            } else {
                products = productService.getAllProductsForUser(page);
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sản phẩm thành công", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage()));
        }
    }

    /**
     * Tìm kiếm sản phẩm theo tên với sắp xếp
     * GET /api/products/search?name=laptop&page=0&sort=price_asc
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sort) {
        try {
            Page<Product> products;
            if (sort != null && !sort.isEmpty()) {
                products = productService.searchProductsByNameForUserWithSort(name, page, sort);
            } else {
                products = productService.searchProductsByNameForUser(name, page);
            }
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm sản phẩm thành công", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage()));
        }
    }

    /**
     * Lọc sản phẩm theo danh mục với sắp xếp
     * GET /api/products/category/1?page=0&sort=price_asc
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sort) {
        try {
            Page<Product> products;
            if (sort != null && !sort.isEmpty()) {
                products = productService.getProductsByCategoryForUserWithSort(categoryId, page, sort);
            } else {
                products = productService.getProductsByCategoryForUser(categoryId, page);
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm theo danh mục thành công", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy sản phẩm theo danh mục: " + e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết sản phẩm
     * GET /api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id)
                    .map(product -> ResponseEntity.ok(ApiResponse.success("Lấy chi tiết sản phẩm thành công", product)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage()));
        }
    }

    /**
     * Lấy sản phẩm liên quan theo danh mục
     * GET /api/products/1/related
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<Page<Product>>> getRelatedProducts(@PathVariable Long id) {
        try {
            Page<Product> relatedProducts = productService.getRelatedProducts(id);
            return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm liên quan thành công", relatedProducts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy sản phẩm liên quan: " + e.getMessage()));
        }
    }
}