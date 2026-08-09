package org.example.service.impl;

import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    // In-memory database
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        users.add(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public User getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> searchUsersByFirstName(String firstName) {
        return users.stream()
                .filter(user -> user.getFirstName().toLowerCase()
                        .contains(firstName.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return existingUser;
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        users.remove(user);
    }

    @Override
    public boolean userExists(Long id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }
}
