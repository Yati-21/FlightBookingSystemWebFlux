package com.flight.request;

import com.flight.entity.AirportCode;
import com.flight.entity.FlightStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightCreateRequest {

	@NotBlank(message="airlineCode is required")
    private String airlineCode;

    @NotBlank(message="flightNumber is required")
    private String flightNumber;

    @NotNull(message="fromCity is required")
    private AirportCode fromCity;

    @NotNull(message="toCity is required")
    private AirportCode toCity;

    @NotNull(message ="departureTime is required")
    private LocalDateTime departureTime;

    @NotNull(message ="arrivalTime is required")
    private LocalDateTime arrivalTime;

    @Min(value=1,message="totalSeats>=1")
    private int totalSeats;


    @Min(value=0, message="price>=0")
    private float price;

    @NotNull(message="status is required")
    private FlightStatus status;

}
