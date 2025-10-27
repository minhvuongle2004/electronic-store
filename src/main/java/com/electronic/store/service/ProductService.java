package com.electronic.store.service;

import com.electronic.store.entity.Product;
import com.electronic.store.entity.Category;
import com.electronic.store.repository.ProductRepository;
import com.electronic.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${app.upload.path:uploads/products}")
    private String uploadPath;

    @Value("${app.upload.url:http://localhost:8080/uploads/products}")
    private String uploadUrl;

    /**
     * Lấy tất cả sản phẩm với phân trang (10 sản phẩm/trang) - cho admin
     */
    public Page<Product> getAllProducts(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable);
    }

    /**
     * Lấy tất cả sản phẩm với phân trang (16 sản phẩm/trang) - cho user
     */
    public Page<Product> getAllProductsForUser(int page) {
        Pageable pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable);
    }

    /**
     * Lấy tất cả sản phẩm với phân trang và sắp xếp theo giá - cho user
     */
    public Page<Product> getAllProductsForUserWithSort(int page, String sortBy) {
        Sort sort;
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by("price").ascending();
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by("price").descending();
        } else {
            sort = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, 16, sort);
        return productRepository.findAll(pageable);
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Tìm kiếm sản phẩm theo tên với phân trang (10 sản phẩm/trang) - cho admin
     */
    public Page<Product> searchProductsByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Tìm kiếm sản phẩm theo tên với phân trang (16 sản phẩm/trang) - cho user
     */
    public Page<Product> searchProductsByNameForUser(String name, int page) {
        Pageable pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Tìm kiếm sản phẩm theo tên với phân trang và sắp xếp - cho user
     */
    public Page<Product> searchProductsByNameForUserWithSort(String name, int page, String sortBy) {
        Sort sort;
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by("price").ascending();
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by("price").descending();
        } else {
            sort = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, 16, sort);
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Lọc sản phẩm theo danh mục với phân trang (10 sản phẩm/trang) - cho admin
     */
    public Page<Product> getProductsByCategory(Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Lọc sản phẩm theo danh mục với phân trang (16 sản phẩm/trang) - cho user
     */
    public Page<Product> getProductsByCategoryForUser(Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Lọc sản phẩm theo danh mục với phân trang và sắp xếp - cho user
     */
    public Page<Product> getProductsByCategoryForUserWithSort(Long categoryId, int page, String sortBy) {
        Sort sort;
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by("price").ascending();
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by("price").descending();
        } else {
            sort = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, 16, sort);
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Thêm sản phẩm mới
     */
    public Product createProduct(Product product) {
        // Kiểm tra tên sản phẩm đã tồn tại
        if (productRepository.existsByName(product.getName())) {
            throw new RuntimeException("Sản phẩm với tên '" + product.getName() + "' đã tồn tại");
        }

        // Kiểm tra danh mục tồn tại
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(product.getCategory().getId());
            if (category.isEmpty()) {
                throw new RuntimeException("Danh mục không tồn tại");
            }
            product.setCategory(category.get());
        }

        return productRepository.save(product);
    }

    /**
     * Cập nhật sản phẩm
     */
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }

        Product existingProduct = optionalProduct.get();

        // Kiểm tra tên sản phẩm trùng lặp (trừ sản phẩm hiện tại)
        if (!existingProduct.getName().equals(productDetails.getName()) &&
            productRepository.existsByName(productDetails.getName())) {
            throw new RuntimeException("Sản phẩm với tên '" + productDetails.getName() + "' đã tồn tại");
        }

        // Kiểm tra danh mục tồn tại
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(productDetails.getCategory().getId());
            if (category.isEmpty()) {
                throw new RuntimeException("Danh mục không tồn tại");
            }
            existingProduct.setCategory(category.get());
        }

        // Cập nhật thông tin
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStock(productDetails.getStock());

        // Chỉ cập nhật imageUrl nếu có giá trị mới
        if (productDetails.getImageUrl() != null) {
            existingProduct.setImageUrl(productDetails.getImageUrl());
        }

        return productRepository.save(existingProduct);
    }

    /**
     * Xóa sản phẩm
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }

        // Lấy thông tin sản phẩm để xóa ảnh
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && product.get().getImageUrl() != null) {
            deleteImageFile(product.get().getImageUrl());
        }

        productRepository.deleteById(id);
    }

    /**
     * Upload ảnh sản phẩm
     */
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File phải là định dạng ảnh (jpg, png, gif)");
        }

        // Kiểm tra kích thước file (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Kích thước file không được vượt quá 5MB");
        }

        try {
            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về relative path thay vì full URL
            return "/uploads/products/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Không thể upload file: " + e.getMessage());
        }
    }

    /**
     * Xóa file ảnh
     */
    private void deleteImageFile(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith("/uploads/products/")) {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadPath, filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // Log error nhưng không throw exception để không ảnh hưởng đến việc xóa sản phẩm
            System.err.println("Không thể xóa file ảnh: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra sản phẩm tồn tại theo ID
     */
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    /**
     * Kiểm tra tên sản phẩm đã tồn tại
     */
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    /**
     * Lấy sản phẩm liên quan theo danh mục (8 sản phẩm, loại trừ sản phẩm hiện tại)
     */
    public Page<Product> getRelatedProducts(Long productId) {
        Optional<Product> currentProduct = productRepository.findById(productId);
        if (currentProduct.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + productId);
        }

        Long categoryId = currentProduct.get().getCategoryId();
        if (categoryId == null) {
            // Nếu sản phẩm không có danh mục, trả về trang rỗng
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(0, 8, Sort.by("createdAt").descending());
        return productRepository.findByCategoryIdAndIdNot(categoryId, productId, pageable);
    }
}