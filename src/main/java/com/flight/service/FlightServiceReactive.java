package com.flight.service;

import com.flight.entity.AirportCode;
import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.request.BookingRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface FlightServiceReactive 
{

    Mono<Flight> addFlight(Flight flight);

    Flux<Flight> searchFlights(AirportCode from, AirportCode to, LocalDate date);

    Mono<Flight> getFlightById(String flightId);

    Mono<String> bookTicket(String flightId,BookingRequest request);

    Mono<Booking> getTicket(String pnr);

    Flux<Booking> getBookingHistoryByUserId(String userId);

    Mono<Void> cancelBooking(String pnr);

    Flux<Flight> getFlightsByAirline(String airlineCode);
}
