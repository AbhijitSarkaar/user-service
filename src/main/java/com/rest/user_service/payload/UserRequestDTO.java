package com.rest.user_service.payload;

import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private List<String> roles;
}

