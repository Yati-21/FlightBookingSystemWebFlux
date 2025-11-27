package com.flight.request;

import com.flight.entity.GENDER;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRequest 
{
    @NotBlank(message="name is required")
    private String name;

    @NotNull(message="gender is required")
    private GENDER gender;

    @Min(value=1,message="age must be >=1")
    @Max(value=120,message="age must be <=120")
    private int age;

    @Pattern(regexp="^[A-Z]\\d+$",message="invalid seat format")
    @NotBlank(message="seatNumber is required")
    private String seatNumber;
}
