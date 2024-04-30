package dev.dipesh.service;

import dev.dipesh.entity.User;

public interface UserService {

    User saveUser(User user);

    User getUserById(String userId);
}
