package com.flight.request;

import java.util.List;

import com.flight.entity.FLIGHT_TYPE;
import com.flight.entity.MEAL_TYPE;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest 
{
    @NotBlank(message="flightId is required")
    private String flightId;

    @NotBlank(message="userId is required")
    private String userId;

    @Min(value=1,message="seatsBooked must be >= 1")
    private int seatsBooked;

    @NotNull(message="mealType is required")
    private MEAL_TYPE mealType;

    @NotNull(message = "flightType is required")
    private FLIGHT_TYPE flightType;
    
    @NotEmpty(message="passengers cannot be empty")
    private List<PassengerRequest> passengers;
}
