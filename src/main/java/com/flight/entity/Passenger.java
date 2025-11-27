package com.flight.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("passengers")
public class Passenger 
{
    @Id
    private String id;

    @NotBlank(message="Passenger name is required")
    private String name;

    @NotNull(message="Gender is required")
    private GENDER gender;

    @Min(value=1)
    @Max(value=120)
    private int age;

    @NotBlank(message="Seat number is required")
    private String seatNumber;

    //Booking table ref
    @NotBlank(message="BookingId is required")
    private String bookingId;
}
