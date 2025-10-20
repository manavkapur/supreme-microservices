package com.supremesolutions.channel_server.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUserService {

    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    public void addUser(String username) {
        activeUsers.add(username);
        System.out.println("👤 User online: " + username);
        printActiveUsers();
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
        System.out.println("❌ User offline: " + username);
        printActiveUsers();
    }

    public boolean isOnline(String username) {
        return activeUsers.contains(username);
    }

    private void printActiveUsers() {
        System.out.println("👥 Active Users: " + activeUsers);
    }
}
