package com.flight.service;

import com.flight.entity.AIRPORT_CODE;
import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.request.BookingRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface FlightServiceReactive 
{
    Mono<Flight> addFlight(Flight flight);

    Flux<Flight> searchFlights(AIRPORT_CODE from, AIRPORT_CODE to, LocalDate date);

    Mono<Flight> getFlightById(String flightId);

    Mono<String> bookTicket(String flightId,BookingRequest request);

    Mono<Booking> getTicket(String pnr);

    Flux<Booking> getBookingHistoryByUserId(String userId);

    Mono<Void> cancelBooking(String pnr);
    
    Flux<Booking> getBookingHistoryByEmail(String email);

    Flux<Flight> getFlightsByAirline(String airlineCode);
}
