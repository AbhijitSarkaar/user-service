
package com.rest.user_service.service;

import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserDetailsDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.security.payload.LogInRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO createUser(UserRequestDTO userRequestDto);

    UserDetailsDTO getUserDetails();

    UserDTO updateUser(UserDTO userDto);

    APIResponse deleteUser();

    ResponseCookie userLogin(LogInRequestDTO logInRequestDto);
}

