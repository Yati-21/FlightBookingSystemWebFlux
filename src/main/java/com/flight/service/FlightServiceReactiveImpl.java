package com.flight.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flight.entity.AirportCode;
import com.flight.entity.Booking;
import com.flight.entity.Flight;
import com.flight.entity.Passenger;
import com.flight.exception.BusinessException;
import com.flight.exception.NotFoundException;
import com.flight.exception.SeatUnavailableException;
import com.flight.repository.AirlineRepository;
import com.flight.repository.BookingRepository;
import com.flight.repository.FlightRepository;
import com.flight.repository.PassengerRepository;
import com.flight.repository.UserRepository;
import com.flight.request.BookingRequest;
import com.flight.request.PassengerRequest;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class FlightServiceReactiveImpl implements FlightServiceReactive 
{

    private static final int CANCELLATION_LIMIT_HOURS=24;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private PassengerRepository passengerRepo;
    
    @Autowired
    private AirlineRepository airlineRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Mono<Flight> addFlight(Flight flight) 
    {
    	return airlineRepo.findById(flight.getAirlineCode())
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .flatMap(al -> 
                {
                	if (flight.getFromCity().equals(flight.getToCity())) {
                        return Mono.error(new BusinessException("source and destination must be different"));
                    }
                    if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
                        return Mono.error(new BusinessException("arrival time must be after departure time"));
                    }

                    if (flight.getDepartureTime().isBefore(LocalDateTime.now())) {
                        return Mono.error(new BusinessException("Flight departure time must be in the future"));
                    }

                    flight.setAvailableSeats(flight.getTotalSeats());

                    return flightRepo.save(flight);
                });
    }
    
    
    @Override
    public Flux<Flight> searchFlights(AirportCode from,AirportCode to,LocalDate date) {
        return flightRepo.findByFromCityAndToCity(from,to)
                .filter(flight->flight.getDepartureTime().toLocalDate().equals(date));
    }


    @Override
    public Mono<Flight> getFlightById(String flightId) 
    {
        return flightRepo.findById(flightId).switchIfEmpty(Mono.error(new NotFoundException("Flight not found")));
    }


    @Override
    public Mono<String> bookTicket(String flightId, BookingRequest request) 
    {
    	return userRepo.findById(request.getUserId())
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .then(flightRepo.findById(flightId)
                .switchIfEmpty(Mono.error(new NotFoundException("Flight not found")))
                .flatMap(flight->
                {    
                	//check if passenger count =seats boooked
                    if (request.getPassengers().size()!= request.getSeatsBooked()) 
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
                    return checkSeatConflicts(flightId,request.getPassengers()).then(saveNewBooking(flight,request));
                }));
    }

    private Mono<String> saveNewBooking(Flight flight,BookingRequest req) 
    {
        String pnr=generateRandomPNR();
        Booking booking =new Booking();
        booking.setPnr(pnr);
        booking.setFlightId(flight.getId());
        booking.setUserId(req.getUserId());
        booking.setSeatsBooked(req.getSeatsBooked());
        booking.setMealType(req.getMealType());
        booking.setFlightType(req.getFlightType());
        booking.setPassengerIds(new ArrayList<>());  //initialize empty list

        return bookingRepo.save(booking)
                .flatMap(savedBooking ->
                    savePassengersAndCollectIds(savedBooking.getId(), req.getPassengers())
                        .flatMap(passengerIds -> {
                            savedBooking.setPassengerIds(passengerIds);
                            flight.setAvailableSeats(flight.getAvailableSeats()-req.getSeatsBooked());
                            return bookingRepo.save(savedBooking)
                                    .then(flightRepo.save(flight))
                                    .thenReturn(savedBooking.getPnr());
                        })
                );
    }
    
    private Mono<List<String>> savePassengersAndCollectIds(String bookingId, List<PassengerRequest> list) {
        return Flux.fromIterable(list).flatMap(req -> 
                {
                    Passenger passenger = new Passenger();
                    passenger.setName(req.getName());
                    passenger.setGender(req.getGender());
                    passenger.setAge(req.getAge());
                    passenger.setSeatNumber(req.getSeatNumber());
                    passenger.setBookingId(bookingId);
                    return passengerRepo.save(passenger);
                })
                .map(Passenger::getId)
                .collectList();
    }


    @Override
    public Mono<Booking> getTicket(String pnr) 
    {
        return bookingRepo.findByPnr(pnr).switchIfEmpty(Mono.error(new NotFoundException("PNR not found")));
    }

    @Override
    public Flux<Booking> getBookingHistoryByUserId(String userId) 
    {
        return bookingRepo.findByUserId(userId);
    }

    @Override
    public Mono<Void> cancelBooking(String pnr) 
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
                                flight.setAvailableSeats(flight.getAvailableSeats()+booking.getSeatsBooked());
                                return passengerRepo.deleteByBookingId(booking.getId())
                                        .then(bookingRepo.delete(booking))
                                        .then(flightRepo.save(flight))
                                        .then();
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
    
    
    private void validatePassengerDuplicateRequest(List<PassengerRequest> passengers) 
    {
        Set<String> seats = new HashSet<>();
        for (PassengerRequest passengerReq:passengers) 
        {
            if (!seats.add(passengerReq.getSeatNumber())) 
            {
                throw new BusinessException("Duplicate seat in request: "+passengerReq.getSeatNumber());
            }
        }
    }


    
    
    private String generateRandomPNR() 
    {
        return "PNR"+UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    

    @Override
    public Flux<Booking> getBookingHistoryByEmail(String email) 
    {
        return userRepo.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with email: "+email)))
                .flatMapMany(user->bookingRepo.findByUserId(user.getId()));
    }

    
    @Override
    public Flux<Flight> getFlightsByAirline(String airlineCode) 
    {
        return flightRepo.findByAirlineCode(airlineCode);
    }
}
