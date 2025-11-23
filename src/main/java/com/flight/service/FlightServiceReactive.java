package com.flight.service;

import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.request.BookingRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface FlightServiceReactive 
{

    Mono<Flight> addFlight(Flight flight);

    Flux<Flight> searchFlights(String from,String to, LocalDate date);

    Mono<Flight> getFlightById(String flightId);

    Mono<String> bookTicket(String flightId,BookingRequest request);

    Mono<Booking> getTicket(String pnr);

    Flux<Booking> getBookingHistoryByUserId(String userId);

    Mono<String> cancelBooking(String pnr);

    Mono<Booking> updateBooking(String pnr,BookingRequest req);

    Flux<Flight> getFlightsByAirline(String airlineCode);
}
