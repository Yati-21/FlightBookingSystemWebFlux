package com.flight.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSearchRequest 
{
    @NotBlank(message="from is required")
    private String from;

    @NotBlank(message="to is required")
    private String to;

    @NotNull(message="date is required")
    private LocalDate date;
}
