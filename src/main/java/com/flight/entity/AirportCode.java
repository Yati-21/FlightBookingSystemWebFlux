package com.flight.entity;

import com.flight.exception.BusinessException;

public enum AirportCode 
{
    DEL,   
    BOM,
    BLR,
    MAA,
    HYD,
    CCU,
    GOI,
    PNQ,
    AMD,
    COK;

//    public static AirportCode fromString(String code) 
//    {
//        try 
//        {
//            return AirportCode.valueOf(code.toUpperCase());
//        } 
//        catch (IllegalArgumentException ex) 
//        {
//            throw new BusinessException("Invalid airport code: "+code);
//        }
//    }
}
