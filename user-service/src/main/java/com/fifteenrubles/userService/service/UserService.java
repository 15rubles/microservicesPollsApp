package com.fifteenrubles.userService.service;

import com.fifteenrubles.userService.entity.User;
import com.fifteenrubles.userService.exception.ApiRequestException;
import com.fifteenrubles.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public User findUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new ApiRequestException("User with id " + userId + " not found", HttpStatus.NOT_FOUND));
    }

    public Optional<User> findUserByIdOptional(Long userId) {
        return userRepository.findUserById(userId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB.isPresent()) {
            throw new ApiRequestException("User with this username exist", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUserAllowedPolls(User user){
        return userRepository.save(user);
    }

}