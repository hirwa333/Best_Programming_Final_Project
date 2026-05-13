package com.impano.logistics.service;

import com.impano.logistics.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AuthService handles user authentication — login and logout.
 * It maintains a simple in-memory user store and tracks the current session.
 *
 * This simulates a database-backed authentication system for the prototype.
 */
public class AuthService {

    // In-memory store: email -> User
    private final Map<String, User> userStore = new HashMap<>();

    // Currently logged-in user (null if no session active)
    private User currentUser = null;

    /**
     * Registers a user into the system so they can log in.
     *
     * @param user the user to register
     */
    public void registerUser(User user) {
        userStore.put(user.getEmail().toLowerCase(), user);
    }

    /**
     * Attempts to log in with the given email and password.
     * Simulates a database lookup and password check.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return Optional containing the logged-in user, or empty if credentials are wrong
     */
    public Optional<User> login(String email, String password) {
        User user = userStore.get(email.toLowerCase());

        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Logs out the current user and clears the session.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return Optional containing current user, or empty if not logged in
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a session is active
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
