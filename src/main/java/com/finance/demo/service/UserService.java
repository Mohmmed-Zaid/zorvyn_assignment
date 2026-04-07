package com.finance.demo.service;

import com.finance.demo.dtos.request.UserRequest;
import com.finance.demo.dtos.response.ApiResponse.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse updateRole(Long id, UserRequest.UpdateRole request);

    UserResponse updateStatus(Long id, UserRequest.UpdateStatus request);

    UserResponse getMyProfile(String email);
}