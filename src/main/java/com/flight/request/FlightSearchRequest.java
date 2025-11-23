package com.flight.request;

import java.time.LocalDate;

import com.flight.entity.AirportCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSearchRequest 
{
    @NotBlank(message="from is required")
    private AirportCode  from;

    @NotBlank(message="to is required")
    private AirportCode to;

    @NotNull(message="date is required")
    private LocalDate date;
}
