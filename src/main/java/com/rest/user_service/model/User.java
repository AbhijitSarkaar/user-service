
package com.rest.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, message = "username needs to be at least 3 characters")
    private String username;

    @NotNull
    @Size(min = 5, message = "password needs to be at least 5 characters")
    private String password;

    @NotNull
    @Email
    @Size(min = 5, message = "email needs to be at least 5 characters")
    private String email;

}
