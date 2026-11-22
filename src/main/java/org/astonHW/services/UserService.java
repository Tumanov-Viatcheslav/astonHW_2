package org.astonHW.services;

import org.astonHW.dao.UserDao;
import org.astonHW.entities.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User create(User user) {
        validateName(user.getName());
        validateEmail(user.getEmail());
        validateAge(user.getAge());
        return userDao.create(user);
    }

    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User update(User user) {
        validateName(user.getName());
        validateEmail(user.getEmail());
        validateAge(user.getAge());
        return userDao.update(user);
    }

    public boolean deleteById(Long id) {
        return userDao.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name required");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Name is too long (max 255)");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Incorrect email: " + email);
        }
        if (email.length() > 255) {
            throw new IllegalArgumentException("Email is too long (max 255)");
        }
    }

    private void validateAge(Integer age) {
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Age should be between 0-150");
        }
    }
}











































































