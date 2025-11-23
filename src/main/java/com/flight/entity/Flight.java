package com.flight.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flights")
public class Flight 
{
    @Id
    private String id;

    //ref to Airline.code
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

    @Min(value=0,message="availableSeats>=0")
    private int availableSeats;

    @Min(value=0, message="price>=0")
    private float price;

    @NotNull(message="status is required")
    private FlightStatus status;

    
    @AssertTrue(message="fromCity and toCity cannot be same")
    public boolean isDifferentCities() 
    {
        return fromCity!=null && toCity!=null && !fromCity.equals(toCity);
    }

    @AssertTrue(message="arrivalTime must be after departureTime")
    public boolean isValidTimes() 
    {
        if (departureTime==null||arrivalTime==null) 
        {
        	return true;
        }
        return arrivalTime.isAfter(departureTime);
    }
}
