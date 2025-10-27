package com.electronic.store.service;

import com.electronic.store.entity.User;
import com.electronic.store.repository.UserRepository;
import com.electronic.store.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        // Kiểm tra user có bị khóa không
        if (user.getStatus() == User.Status.BLOCKED) {
            throw new UsernameNotFoundException("User account is blocked: " + username);
        }

        // Chuyển đổi User entity thành UserPrincipal
        return UserPrincipal.create(user);
    }

    // Method tùy chỉnh để load user theo ID (có thể dùng cho JWT)
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (user.getStatus() == User.Status.BLOCKED) {
            throw new UsernameNotFoundException("User account is blocked with id: " + id);
        }

        return UserPrincipal.create(user);
    }

    // Method kiểm tra user có tồn tại và active không
    public boolean isUserExistAndActive(String username) {
        return userRepository.findByUsernameAndStatus(username, User.Status.ACTIVE).isPresent();
    }

    // Method lấy thông tin user entity (không phải UserDetails)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
    }
}