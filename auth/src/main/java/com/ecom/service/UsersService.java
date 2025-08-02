package com.ecom.service;

import com.ecom.request.UserRequestDto;
import com.ecom.response.ApiResponse;
import com.ecom.response.UserResponseDto;

import java.util.List;

public interface UsersService {

    ApiResponse<UserResponseDto> createUser(UserRequestDto userRequestDto);

    ApiResponse<List<UserResponseDto>> getAllUsers();

    ApiResponse<UserResponseDto> getUserById(Long id);

    String verify(UserRequestDto userRequestDto);
}
