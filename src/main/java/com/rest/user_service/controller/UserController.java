
package com.rest.user_service.controller;

import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserDetailsDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.security.jwt.JwtUtils;
import com.rest.user_service.security.payload.LogInRequestDTO;
import com.rest.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/public/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/auth/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDto) {
        return new ResponseEntity<>(
                userService.createUser(userRequestDto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsDTO> getUserDetails() {
        return new ResponseEntity<>(userService.getUserDetails(), HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<APIResponse> updateUser(
            @Valid @RequestBody UserDTO userDto
    ) {
        userService.updateUser(userDto);
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new APIResponse("User details updated"));
    }

    @DeleteMapping("/users")
    public ResponseEntity<APIResponse> deleteUser() {
        APIResponse response = userService.deleteUser();
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequestDTO logInRequestDto) {
        ResponseCookie cookie = userService.userLogin(logInRequestDto);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new APIResponse("User logged in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new APIResponse("User logged out"));
    }

}
