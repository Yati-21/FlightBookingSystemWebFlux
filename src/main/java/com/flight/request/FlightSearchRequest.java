package com.flight.request;

import java.time.LocalDate;

import com.flight.entity.AIRPORT_CODE;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSearchRequest 
{
    @NotNull(message="from is required")
    private AIRPORT_CODE  from;

    @NotNull(message="to is required")
    private AIRPORT_CODE to;

    @NotNull(message="date is required")
    private LocalDate date;
}
