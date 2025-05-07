package com.pm.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDTO(@NotBlank(message = "Email is required") @Email(message = "Email shoould be valid email address") String email,
                                  @NotBlank(message = "Password is required") @Size(min = 8, message = "password should be at least 8 characters") String password) {
}
