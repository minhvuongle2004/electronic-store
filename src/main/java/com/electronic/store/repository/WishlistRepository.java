package com.electronic.store.repository;

import com.electronic.store.entity.Product;
import com.electronic.store.entity.User;
import com.electronic.store.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserAndProduct(User user, Product product);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    boolean existsByUserAndProduct(User user, Product product);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}