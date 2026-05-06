
package com.rest.user_service.controller;

import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.payload.UserDTO;
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

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDto) {
        return new ResponseEntity<>(
                userService.createUser(userRequestDto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
            return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Valid @RequestBody UserDTO userDto,
            @PathVariable("userId") Long userId
    ) {
        return new ResponseEntity<>(
                userService.updateUser(userDto, userId),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable("userId") Long userid) {
        return new ResponseEntity<>(
                userService.deleteUser(userid),
                HttpStatus.OK
        );
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequestDTO logInRequestDto) {
        ResponseCookie cookie = userService.userLogin(logInRequestDto);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new APIResponse("User logged in"));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new APIResponse("User logged out"));
    }

}
