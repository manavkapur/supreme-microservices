package com.supremesolutions.userservice.service;

import com.supremesolutions.userservice.model.User;
import com.supremesolutions.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<String> getFcmToken(Long userId) {
        return userRepository.findById(userId).map(User::getFcmToken);
    }

    public boolean registerFcmToken(Long userId, String fcmToken) {
        return userRepository.findById(userId).map(user -> {
            user.setFcmToken(fcmToken);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}
