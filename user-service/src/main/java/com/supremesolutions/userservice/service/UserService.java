package com.supremesolutions.userservice.service;

import com.supremesolutions.userservice.entity.User;
import com.supremesolutions.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered!");
        }

        return userRepository.save(user);
    }

    public Optional<String> getFcmToken(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFcmToken);
    }

    public boolean registerFcmToken(Long userId, String fcmToken) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setFcmToken(fcmToken);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
