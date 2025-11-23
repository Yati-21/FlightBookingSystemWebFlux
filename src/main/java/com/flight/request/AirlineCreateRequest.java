package com.flight.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AirlineCreateRequest 
{

    @NotBlank(message="Airline code is required")
    private String code;

    @NotBlank(message="Airline name is required")
    private String name;
}
