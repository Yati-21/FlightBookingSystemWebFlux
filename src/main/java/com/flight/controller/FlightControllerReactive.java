package com.flight.controller;

import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.request.BookingRequest;
import com.flight.request.FlightSearchRequest;
import com.flight.service.FlightServiceReactive;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/")
public class FlightControllerReactive 
{

    @Autowired
    private FlightServiceReactive service;

    //add a new flight
    @PostMapping("/flights/add")
    public Mono<Flight> addFlight(@RequestBody @Valid Flight flight) 
    {
        return service.addFlight(flight);
    }

    //serach flight using to from date
    @PostMapping("/flights/search")
    public Flux<Flight> searchFlights(@RequestBody @Valid FlightSearchRequest request) 
    {
        return service.searchFlights(request.getFrom(),request.getTo(),request.getDate());
    }

    //get particular flight using flightid
    @GetMapping("/flights/get/{flightId}")
    public Mono<Flight> getFlight(@PathVariable String flightId) 
    {
        return service.getFlightById(flightId);
    }

    //add booking
    @PostMapping("/bookings/create")
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

//    //update booking
//    @PutMapping("/bookings/update/{pnr}")
//    public Mono<Booking> updateBooking(@PathVariable String pnr,@RequestBody @Valid BookingRequest request) 
//    {
//        return service.updateBooking(pnr,request);
//    }

    //cancel booking
    @DeleteMapping("/bookings/cancel/{pnr}")
    public Mono<String> cancelBooking(@PathVariable String pnr) 
    {
        return service.cancelBooking(pnr);
    }

    //all bookings done by a user - using email 
    @GetMapping("/bookings/history/user/{userId}")
    public Flux<Booking> getUserBookings(@PathVariable String userId) 
    {
        return service.getBookingHistoryByUserId(userId);
    }


    //get all flights of particular airline - enter airline code
    @GetMapping("/airlines/{code}/flights")
    public Flux<Flight> getFlightsByAirline(@PathVariable String code) {
        return service.getFlightsByAirline(code);
    }
}
