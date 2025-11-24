package com.flight.controller;

import com.flight.entity.*;
import com.flight.request.BookingRequest;
import com.flight.request.FlightSearchRequest;
import com.flight.request.PassengerRequest;
import com.flight.service.FlightServiceReactive;
import com.flight.exception.GlobalErrorHandler;
import com.flight.exception.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers=FlightControllerReactive.class)
@Import(GlobalErrorHandler.class)
class FlightControllerReactiveTest 
{
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private FlightServiceReactive service;

    
    private Flight testFlight;
    private Booking testBooking;

    @BeforeEach
    void setup() 
    {
        testFlight =new Flight();
        testFlight.setId("F1");
        testFlight.setAirlineCode("AI");
        testFlight.setFlightNumber("AI100");
        testFlight.setFromCity(AirportCode.DEL);
        testFlight.setToCity(AirportCode.BOM);
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testFlight.setTotalSeats(100);
        testFlight.setAvailableSeats(100);
        testFlight.setPrice(5000);
        testFlight.setStatus(FlightStatus.SCHEDULED);
        
        testBooking=new Booking();
        testBooking.setId("B1");
        testBooking.setPnr("pnr1");
        testBooking.setFlightId("F1");
        testBooking.setUserId("user1");
    }

    @Test
    void addFlightSuccess() 
    {
        when(service.addFlight(any(Flight.class))).thenReturn(Mono.just(testFlight));
        webTestClient.post().uri("/flights/add").contentType(MediaType.APPLICATION_JSON).bodyValue(testFlight)
            .exchange().expectStatus().isCreated().expectBody(String.class).isEqualTo("F1");
    }

    @Test
    void addFlightAirlineNotFound() 
    {
        when(service.addFlight(any(Flight.class))).thenReturn(Mono.error(new NotFoundException("airline not found")));
        webTestClient.post().uri("/flights/add").contentType(MediaType.APPLICATION_JSON).bodyValue(testFlight)
                .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.error").isEqualTo("airline not found");
    }

    @Test
    void searchFlightsSuccess() 
    {
        FlightSearchRequest req=new FlightSearchRequest();
        req.setFrom(AirportCode.DEL);
        req.setTo(AirportCode.BOM);
        req.setDate(LocalDate.now().plusDays(1));
        when(service.searchFlights(AirportCode.DEL,AirportCode.BOM,req.getDate())).thenReturn(Flux.just(testFlight));
        webTestClient.post().uri("/flights/search").contentType(MediaType.APPLICATION_JSON).bodyValue(req)
                .exchange().expectStatus().isOk().expectBodyList(String.class).hasSize(1).contains("F1");
    }

    @Test
    void getFlightByIdSuccess() 
    {
        when(service.getFlightById("F1")).thenReturn(Mono.just(testFlight));

        webTestClient.get().uri("/flights/get/F1").exchange().expectStatus().isOk().expectBody()
            .jsonPath("$.flightNumber").isEqualTo("AI100");
    }

    @Test
    void createBookingSuccess() {

        BookingRequest req=new BookingRequest();
        req.setFlightId("F1");
        req.setUserId("user1");
        req.setSeatsBooked(1);
        req.setMealType(MealType.VEG);
        req.setFlightType(FlightType.ONE_WAY);

        PassengerRequest p=new PassengerRequest();
        p.setName("A");
        p.setGender(Gender.M);
        p.setAge(22);
        p.setSeatNumber("A1");

        req.setPassengers(List.of(p));

        when(service.bookTicket(eq("F1"),any(BookingRequest.class))).thenReturn(Mono.just("pnr123"));

        webTestClient.post()
                .uri("/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("pnr123");
    }


    @Test
    void getBookingSuccess() 
    {
        when(service.getTicket("pnr1")).thenReturn(Mono.just(testBooking));
        webTestClient.get().uri("/bookings/get/pnr1").exchange().expectStatus().isOk().expectBody()
        	.jsonPath("$.pnr").isEqualTo("pnr1");
    }

    @Test
    void cancelBookingSuccess() {
        when(service.cancelBooking("pnr1")).thenReturn(Mono.empty());
        webTestClient.delete().uri("/bookings/cancel/pnr1").exchange().expectStatus().isOk();
    }

    @Test
    void getBookingHistoryByEmailSuccess() {
        when(service.getBookingHistoryByEmail("a@test.com")).thenReturn(Flux.just(testBooking));

        webTestClient.get().uri("/bookings/history/email/a@test.com").exchange()
            .expectStatus().isOk().expectBody()
            .jsonPath("$[0].pnr").isEqualTo("pnr1");
    }

    @Test
    void getUserBookingsSuccess() 
    {
        when(service.getBookingHistoryByUserId("user1")).thenReturn(Flux.just(testBooking));
        webTestClient.get().uri("/bookings/history/user/user1")
	                .exchange().expectStatus().isOk().expectBody()
	                .jsonPath("$[0].pnr").isEqualTo("pnr1");
    }

    @Test
    void getFlightsByAirlineSuccess() 
    {
        when(service.getFlightsByAirline("AI")).thenReturn(Flux.just(testFlight));

        webTestClient.get().uri("/airlines/AI/flights").exchange()
                .expectStatus().isOk().expectBody()
                .jsonPath("$[0].id").isEqualTo("F1");
    }
    
    
    
    
}
