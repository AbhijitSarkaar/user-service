
package com.rest.user_service.security.payload;

import lombok.Data;

@Data
public class LogInRequestDTO {
    String username;
    String password;
}
