package org.example.service;

import org.example.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> searchUsersByFirstName(String firstName);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    boolean userExists(Long id);
}
