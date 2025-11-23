package com.flight.service;

import com.flight.entity.*;
import com.flight.exception.BusinessException;
import com.flight.exception.NotFoundException;
import com.flight.exception.SeatUnavailableException;
import com.flight.repository.BookingRepository;
import com.flight.repository.FlightRepository;
import com.flight.repository.PassengerRepository;
import com.flight.repository.UserRepository;
import com.flight.request.BookingRequest;
import com.flight.request.PassengerRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class FlightServiceReactiveImpl implements FlightServiceReactive 
{

    private static final long CANCELLATION_LIMIT_HOURS = 24;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private UserRepository userRepo;


    @Override
    public Mono<Flight> addFlight(Flight flight) 
    {
        //when a flight is created total = available seats
        flight.setAvailableSeats(flight.getTotalSeats());
        return flightRepo.save(flight);
    }

    @Override
    public Flux<Flight> searchFlights(String from,String to,LocalDate date) 
    {    
        AirportCode fromCity=AirportCode.fromString(from);
        AirportCode toCity=AirportCode.fromString(to);

        //find flights 
        return flightRepo.findByFromCityAndToCity(fromCity, toCity).filter(f->f.getDepartureTime().toLocalDate().equals(date));
    }

    @Override
    public Mono<Flight> getFlightById(String flightId) 
    {
        return flightRepo.findById(flightId).switchIfEmpty(Mono.error(new NotFoundException("Flight not found")));
    }


    @Override
    public Mono<String> bookTicket(String flightId, BookingRequest request) 
    {
        return flightRepo.findById(flightId)
                .switchIfEmpty(Mono.error(new NotFoundException("Flight not found")))
                .flatMap(flight->
                {    
                	//check if passenger count =seats boooked
                    if (request.getPassengers().size() != request.getSeatsBooked()) 
                    {
                        return Mono.error(new BusinessException("Passengers count must be equal to seats booked"));
                    }
                    
                    //check seat availability
                    if (flight.getAvailableSeats()<request.getSeatsBooked()) 
                    {
                        return Mono.error(new SeatUnavailableException("Not enough seats available"));
                    }
                    
                    //check for duplicate passengers in the request
                    validatePassengerDuplicateRequest(request.getPassengers());
                    
                    //check seat conflicts with existing bookings
                    return checkSeatConflicts(flightId, request.getPassengers()).then(saveNewBooking(flight, request));
                });
    }

    private Mono<String> saveNewBooking(Flight flight, BookingRequest req) 
    {
        String pnr= generateRandomPNR();

        Booking booking =new Booking();
        booking.setPnr(pnr);
        booking.setFlightId(flight.getId());
        booking.setUserId(req.getUserId());
        booking.setSeatsBooked(req.getSeatsBooked());
        booking.setMealType(req.getMealType());

        return bookingRepo.save(booking)
                .flatMap(savedBooking-> 
                {
                    //reduce flight available seats
                    flight.setAvailableSeats(flight.getAvailableSeats() -req.getSeatsBooked());

                    return flightRepo.save(flight).then(savePassengers(savedBooking.getId(), req.getPassengers())).thenReturn(savedBooking.getPnr());
                });
    }

    private Mono<Void> savePassengers(String bookingId, List<PassengerRequest> passengers) {
        return Flux.fromIterable(passengers)
                .flatMap(req -> 
                {
                    Passenger passenger=new Passenger();
                    passenger.setName(req.getName());
                    passenger.setGender(Gender.valueOf(req.getGender()));
                    passenger.setAge(req.getAge());
                    passenger.setSeatNumber(req.getSeatNumber());
                    passenger.setBookingId(bookingId);
                    return passengerRepo.save(passenger);
                })
                .then();
    }


    @Override
    public Mono<Booking> getTicket(String pnr) 
    {
        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new NotFoundException("PNR not found")));
    }

    @Override
    public Flux<Booking> getBookingHistoryByUserId(String userId) 
    {
        return bookingRepo.findByUserId(userId);
    }

    @Override
    public Mono<String> cancelBooking(String pnr) 
    {
        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new NotFoundException("Invalid PNR")))
                .flatMap(booking-> 
                {
                    return flightRepo.findById(booking.getFlightId()).flatMap(flight-> 
                            {
                                long hoursDiff=Duration.between(LocalDateTime.now(),flight.getDepartureTime()).toHours();
                                if (hoursDiff<CANCELLATION_LIMIT_HOURS) 
                                {
                                    return Mono.error(new BusinessException("Cannot cancel within 24 hours of departure"));
                                }
                                //restore seats
                                flight.setAvailableSeats(flight.getAvailableSeats()+booking.getSeatsBooked());

                                return passengerRepo.deleteByBookingId(booking.getId())
                                        .then(bookingRepo.delete(booking))
                                        .then(flightRepo.save(flight))
                                        .thenReturn("Booking cancelled: "+booking.getPnr());
                            });
                });
    }





    private Mono<Void> checkSeatConflicts(String flightId, List<PassengerRequest> newPassengers) 
    {
        return bookingRepo.findByFlightId(flightId)
                .flatMap(booking-> passengerRepo.findByBookingId(booking.getId())).map(Passenger::getSeatNumber).collectList()
                .flatMap(existingSeats-> 
                {
                    for (PassengerRequest passengerReq :newPassengers) 
                    {
                        if (existingSeats.contains(passengerReq.getSeatNumber())) 
                        {
                            return Mono.error(new SeatUnavailableException("Seat already booked: "+passengerReq.getSeatNumber()));
                        }
                    }
                    return Mono.empty();
                });
    }
    
        private Mono<Void> checkSeatConflicts(String flightId, List<PassengerRequest> newPassengers, String excludeBookingId) 
    {
        return bookingRepo.findByFlightId(flightId)
                .filter(b->!b.getId().equals(excludeBookingId))
                .flatMap(b ->passengerRepo.findByBookingId(b.getId()))
                .map(Passenger::getSeatNumber)
                .collectList()
                .flatMap(existingSeats-> 
                {
                    for (PassengerRequest passengerReq :newPassengers) 
                    {
                        if (existingSeats.contains(passengerReq.getSeatNumber())) 
                        {
                            return Mono.error(new SeatUnavailableException("Seat already booked: "+passengerReq.getSeatNumber()));
                        }
                    }
                    return Mono.empty();
                });
    }



    private void validatePassengerDuplicateRequest(List<PassengerRequest> passengers) 
    {
        Set<String> set=new HashSet<>();
        for (PassengerRequest passengerReq:passengers) {
            String key=passengerReq.getName()+"-"+passengerReq.getAge() +"-"+passengerReq.getGender();
            if (!set.add(key)) 
            {
                throw new BusinessException("Duplicate passenger: "+passengerReq.getName());
            }
        }
    }
    
    
    private String generateRandomPNR() 
    {
        return "PNR"+new Random().nextInt(90000);
    }
    

    @Override
    public Flux<Flight> getFlightsByAirline(String airlineCode) 
    {
        return flightRepo.findByAirlineCode(airlineCode);
    }
}
