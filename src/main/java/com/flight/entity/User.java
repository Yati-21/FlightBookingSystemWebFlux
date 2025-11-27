package com.flight.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class User 
{
    @Id
    private String id;

    @NotBlank(message="User name is required")
    private String name;

    @Email(message="Invalid email format")
    @NotBlank(message="Email is required")
    private String email;
}