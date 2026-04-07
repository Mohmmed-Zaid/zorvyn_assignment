package com.finance.demo.service;

import com.finance.demo.dtos.request.UserRequest;
import com.finance.demo.dtos.response.ApiResponse.UserResponse;
import com.finance.demo.entities.User;
import com.finance.demo.exception.AppException;
import com.finance.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public UserResponse updateRole(Long id, UserRequest.UpdateRole request) {
        User user = findOrThrow(id);
        user.setRole(request.getRole());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateStatus(Long id, UserRequest.UpdateStatus request) {
        User user = findOrThrow(id);
        user.setActive(request.getActive());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {
        return toResponse(
                userRepository.findByEmail(email.trim().toLowerCase())
                        .orElseThrow(() ->
                                new AppException.ResourceNotFoundException("User not found")
                        )
        );
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.ResourceNotFoundException.forUser(id));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}