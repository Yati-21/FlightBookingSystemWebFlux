package com.flight.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("bookings")
public class Booking {

    @Id
    private String id;

    @NotBlank(message="PNR is required")
    private String pnr;

    //from user table
    @NotBlank(message="UserId is required")
    private String userId;

    //from flihgt table
    @NotBlank(message="FlightId is required")
    private String flightId;

    @Min(value=1,message="At least 1 seat must be booked")
    private int seatsBooked;

    @NotNull(message="Meal type is required")
    private MealType mealType;
    
    private List<String> passengerIds;
}
