package com.flight.service;

import com.flight.entity.*;
import com.flight.exception.BusinessException;
import com.flight.exception.NotFoundException;
import com.flight.repository.*;
import com.flight.request.BookingRequest;
import com.flight.request.PassengerRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceReactiveImplTest 
{
    @Mock
    private FlightRepository flightRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private PassengerRepository passengerRepo;
    @Mock
    private AirlineRepository airlineRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private FlightServiceReactiveImpl service;

    private Flight validFlight;

    @BeforeEach
    void setup() 
    {
        validFlight=new Flight();
        validFlight.setId("flight1");
        validFlight.setAirlineCode("ai");
        validFlight.setFromCity(AirportCode.DEL);
        validFlight.setToCity(AirportCode.BOM);
        validFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        validFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));
        validFlight.setTotalSeats(100);
        validFlight.setAvailableSeats(100);
        validFlight.setPrice(5000);
        validFlight.setStatus(FlightStatus.SCHEDULED);
        validFlight.setFlightNumber("ai101");
    }

    @Test
    void testAddFlightSuccess() 
    {
        when(airlineRepo.findById("ai")).thenReturn(Mono.just(new Airline("ai","air india")));
        when(flightRepo.save(any())).thenReturn(Mono.just(validFlight));
        StepVerifier.create(service.addFlight(validFlight)).expectNext(validFlight).verifyComplete();
    }

    
    @Test
    void testAddFlightArrivalBeforeDeparture() 
    {
        validFlight.setArrivalTime(validFlight.getDepartureTime().minusHours(1));
        when(airlineRepo.findById("ai")).thenReturn(Mono.just(new Airline("ai" ,"air india")));
        StepVerifier.create(service.addFlight(validFlight)).expectErrorMatches(ex-> ex instanceof BusinessException&&ex.getMessage().contains("Arrival time must be after departure time")).verify();
    }


    //searchFlights()
    @Test
    void testSearchFlights() 
    {
        validFlight.setDepartureTime(LocalDateTime.of(2030,1,1, 10,0));
        LocalDate date=LocalDate.of(2030 ,1, 1);
        when(flightRepo.findByFromCityAndToCity(AirportCode.DEL, AirportCode.BOM)).thenReturn(Flux.just(validFlight));
        StepVerifier.create(service.searchFlights(AirportCode.DEL,AirportCode.BOM, date)).expectNext(validFlight) .verifyComplete();
    }

    //getFlightById()
    @Test
    void testGetFlightByIdSuccess() 
    {
        when(flightRepo.findById("flight1")).thenReturn(Mono.just(validFlight));
        StepVerifier.create(service.getFlightById("flight1")).expectNext(validFlight).verifyComplete();
    }


    //bookTicket()
    private BookingRequest createValidBookingRequest() 
    {
        BookingRequest req=new BookingRequest();
        req.setFlightId("flight1");
        req.setUserId("user1");
        req.setSeatsBooked(2);
        req.setMealType(MealType.VEG);
        req.setFlightType(FlightType.ONE_WAY);

        PassengerRequest passReq1=new PassengerRequest();
        passReq1.setName("A");
        passReq1.setGender(Gender.M);
        passReq1.setAge(20);
        passReq1.setSeatNumber("A1");
        PassengerRequest passReq2=new PassengerRequest();
        passReq2.setName("B");
        passReq2.setGender(Gender.F);
        passReq2.setAge(22);
        passReq2.setSeatNumber("A2");

        req.setPassengers(List.of(passReq1,passReq2));
        return req;
    }

    @Test
    void testBookTicketSuccess() 
    {
        BookingRequest req =createValidBookingRequest();

        Booking saved= new Booking();
        saved.setId("bookId1");
        saved.setPnr("PNR0000");
        saved.setFlightId("flight1");
        saved.setUserId("user1");
        saved.setSeatsBooked(2);

        when(userRepo.findById("user1")).thenReturn(Mono.just(new User()));
        when(flightRepo.findById("flight1")).thenReturn(Mono.just(validFlight));
        when(bookingRepo.findByFlightId("flight1")).thenReturn(Flux.empty());
        when(passengerRepo.save(any())).thenAnswer(inv->
        {
            Passenger p= inv.getArgument(0);
            p.setId(UUID.randomUUID().toString());
            return Mono.just(p);
        });
        when(bookingRepo.save(any())).thenReturn(Mono.just(saved));
        when(flightRepo.save(any())).thenReturn(Mono.just(validFlight));

        StepVerifier.create(service.bookTicket("flight1",req)).expectNextCount(1).verifyComplete();
    }


    //cancelBooking()
    @Test
    void testCancelBookingSuccess() 
    {
        Booking booking=new Booking();
        booking.setId("bookId1");
        booking.setFlightId("flight1");
        booking.setSeatsBooked(2);

        validFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        booking.setPnr("PNR123");

        when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(booking));
        when(flightRepo.findById("flight1")).thenReturn(Mono.just(validFlight));
        when(passengerRepo.deleteByBookingId("bookId1")).thenReturn(Mono.empty());
        when(bookingRepo.delete(booking)).thenReturn(Mono.empty());
        when(flightRepo.save(validFlight)).thenReturn(Mono.just(validFlight));
        StepVerifier.create(service.cancelBooking("PNR123")).verifyComplete();
    }


    //getBookingHistoryByEmail()
    @Test
    void testGetBookingHistoryByEmailSuccess() 
    {
        User user=new User("user1","A", "a@test.com");
        Booking booking =new Booking();
        booking .setPnr("PNR100");
        booking .setUserId("user1");
        when(userRepo.findByEmail("a@test.com")).thenReturn(Mono.just(user));
        when(bookingRepo.findByUserId("user1")).thenReturn(Flux.just(booking ));
        StepVerifier.create(service.getBookingHistoryByEmail("a@test.com")).expectNext(booking ).verifyComplete();
    }


    //getFlightsByAirline()
    @Test
    void testGetFlightsByAirline() 
    {
        when(flightRepo.findByAirlineCode("ai")).thenReturn(Flux.just(validFlight));
        StepVerifier.create(service.getFlightsByAirline("ai")).expectNext(validFlight).verifyComplete();
    }
}
