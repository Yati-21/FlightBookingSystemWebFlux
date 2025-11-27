package com.flight.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest 
{
    @NotBlank(message="User name is required")
    private String name;

    @Email(message="Invalid email format")
    @NotBlank(message="Email is required")
    private String email;
}
