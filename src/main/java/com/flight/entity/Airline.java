package com.flight.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="airlines")
public class Airline
{
    @Id
    @NotBlank(message="Airline code is required")
    private String code;

    @NotBlank(message="Airline name is required")
    private String name;
}
