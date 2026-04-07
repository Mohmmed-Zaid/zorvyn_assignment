package com.finance.demo.security;

import com.finance.demo.dtos.response.ApiResponse.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public abstract class UserService {
    @Transactional(readOnly = true)
    public abstract Page<UserResponse> getAllUsers(Pageable pageable);
}
