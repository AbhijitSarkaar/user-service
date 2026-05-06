
package com.rest.user_service.service;

import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.security.payload.LogInRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO createUser(UserRequestDTO userRequestDto);

    UserDTO getUserById(Long userId);

    UserDTO updateUser(UserDTO userDto, Long userId);

    APIResponse deleteUser(Long userid);

    ResponseCookie userLogin(LogInRequestDTO logInRequestDto);
}

