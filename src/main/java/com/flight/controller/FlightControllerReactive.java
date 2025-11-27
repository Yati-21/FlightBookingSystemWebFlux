package com.flight.controller;

import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.request.BookingRequest;
import com.flight.request.FlightSearchRequest;
import com.flight.service.FlightServiceReactive;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Slf4j
@RestController
@RequestMapping("/")
public class FlightControllerReactive 
{
	//using constructor injection instead of autowired - sonarqube suggestion
	private final FlightServiceReactive service;
    public FlightControllerReactive(FlightServiceReactive service) 
    {
        this.service=service;
    }
    
    //add a new flight
    @PostMapping("/flights/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> addFlight(@RequestBody @Valid Flight flight) {
        return service.addFlight(flight).map(Flight::getId);
    }

    //serach flight using to from date
    @PostMapping("/flights/search")
    public Flux<String> searchFlights(@RequestBody @Valid FlightSearchRequest request) 
    {
        return service.searchFlights(request.getFrom(),request.getTo(),request.getDate()).map(Flight::getId);
    }

    //get particular flight using flightid
    @GetMapping("/flights/get/{flightId}")
    public Mono<Flight> getFlight(@PathVariable String flightId) 
    {
        return service.getFlightById(flightId);
    }

    //add booking
    @PostMapping("/bookings/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createBooking(@RequestBody @Valid BookingRequest request) 
    {
        return service.bookTicket(request.getFlightId(),request);
    }

    //get booking using pnr
    @GetMapping("/bookings/get/{pnr}")
    public Mono<Booking> getBooking(@PathVariable String pnr) 
    {
        return service.getTicket(pnr);
    }
    
    //cancel booking
    @DeleteMapping("/bookings/cancel/{pnr}")
    public Mono<Void> cancelBooking(@PathVariable String pnr) 
    {
        return service.cancelBooking(pnr);
    }
    
    @GetMapping("/bookings/history/email/{email}")
    public Flux<Booking> getBookingHistoryByEmail(@PathVariable String email) {
        return service.getBookingHistoryByEmail(email);
    }


    //all bookings done by a user using user id 
    @GetMapping("/bookings/history/user/{userId}")
    public Flux<Booking> getUserBookings(@PathVariable String userId) 
    {
        return service.getBookingHistoryByUserId(userId);
    }

    //get all flights of particular airline enter airline code
    @GetMapping("/airlines/{code}/flights")
    public Flux<Flight> getFlightsByAirline(@PathVariable String code) {
        return service.getFlightsByAirline(code);
    }
}
