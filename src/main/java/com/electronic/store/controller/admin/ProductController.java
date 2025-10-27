package com.electronic.store.controller.admin;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Product;
import com.electronic.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Lấy tất cả sản phẩm với phân trang
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Product> products = productService.getAllProducts(page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách sản phẩm thành công", products)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy danh sách sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy sản phẩm theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Lấy thông tin sản phẩm thành công", product.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Không tìm thấy sản phẩm với ID: " + id, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lấy thông tin sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Product> products = productService.searchProductsByName(name, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Tìm kiếm sản phẩm thành công", products)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi tìm kiếm sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Lọc sản phẩm theo danh mục
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Product> products = productService.getProductsByCategory(categoryId, page);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Lọc sản phẩm theo danh mục thành công", products)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi lọc sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Thêm sản phẩm mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Thêm sản phẩm thành công", createdProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi thêm sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Cập nhật sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Cập nhật sản phẩm thành công", updatedProduct)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi cập nhật sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Xóa sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Xóa sản phẩm thành công", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi xóa sản phẩm: " + e.getMessage(), null));
        }
    }

    /**
     * Upload ảnh sản phẩm
     */
    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = productService.uploadImage(file);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Upload ảnh thành công", imageUrl)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi upload ảnh: " + e.getMessage(), null));
        }
    }

    /**
     * Kiểm tra tên sản phẩm đã tồn tại
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<ApiResponse<Boolean>> checkProductNameExists(@PathVariable String name) {
        try {
            boolean exists = productService.existsByName(name);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Kiểm tra tên sản phẩm thành công", exists)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Lỗi kiểm tra tên sản phẩm: " + e.getMessage(), null));
        }
    }
}