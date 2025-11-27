package com.flight.exception;


//used if seat already booked
//or if not enough seat available

public class SeatUnavailableException extends RuntimeException 
{
    public SeatUnavailableException(String message) 
    {
        super(message);
    }
}
