package com.electronic.store.service;

import com.electronic.store.entity.Product;
import com.electronic.store.entity.User;
import com.electronic.store.entity.Wishlist;
import com.electronic.store.repository.ProductRepository;
import com.electronic.store.repository.UserRepository;
import com.electronic.store.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public boolean addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user != null && product != null) {
            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);
            return true;
        }
        return false;
    }

    public boolean removeFromWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return true;
        }
        return false;
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    public long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    public void clearWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        wishlistRepository.deleteAll(wishlists);
    }
}