package com.rest.user_service.payload;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
