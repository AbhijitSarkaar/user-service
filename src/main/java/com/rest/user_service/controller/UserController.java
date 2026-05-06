
package com.rest.user_service.controller;

import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

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
}
