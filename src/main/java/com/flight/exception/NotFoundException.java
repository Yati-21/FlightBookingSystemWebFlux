package com.flight.exception;

//used in : flight/pnr/user Not found

public class NotFoundException extends RuntimeException 
{
    public NotFoundException(String message) 
    {
        super(message);
    }
}
