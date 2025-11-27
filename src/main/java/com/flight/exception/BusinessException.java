package com.flight.exception;

//used in 
	//duplicate passengers
	//violating booking rules
	//trying to update/cancel within 24 hours
	//invalid airport codes

public class BusinessException extends RuntimeException 
{
    public BusinessException(String message) 
    {
        super(message);
    }
}
