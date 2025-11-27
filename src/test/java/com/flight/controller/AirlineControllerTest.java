package com.flight.controller;

import com.flight.entity.Airline;
import com.flight.repository.AirlineRepository;
import com.flight.request.AirlineCreateRequest;
import com.flight.request.AirlineUpdateRequest;
import com.flight.exception.GlobalErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@org.springframework.boot.webflux.test.autoconfigure.WebFluxTest(controllers=AirlineController.class)
@Import(GlobalErrorHandler.class)
class AirlineControllerTest 
{
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AirlineRepository airlineRepo;

    private Airline sample;

    @BeforeEach
    void setup() 
    {
        sample =new Airline("AI","Air india");
    }

    @Test
    void createAirlineSuccess() 
    {
        AirlineCreateRequest req= new AirlineCreateRequest();
        req.setCode("AI");
        req.setName("Air india");
        when(airlineRepo.save(any(Airline.class))).thenReturn(Mono.just(sample));
        webTestClient.post()
                .uri("/airlines").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req).exchange().expectStatus().isCreated().expectBody(String.class).isEqualTo("AI");
    }

    @Test
    void getAllAirlines() 
    {
        when(airlineRepo.findAll()).thenReturn(Flux.just(sample));
        webTestClient.get()
                .uri("/airlines")
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$[0].code").isEqualTo("AI");
    }

    @Test
    void updateAirlineSuccess() 
    {
        AirlineUpdateRequest req =new AirlineUpdateRequest();
        req.setName("Ai update");
        Airline updated= new Airline("AI","Ai update");
        when(airlineRepo.findById("AI")).thenReturn(Mono.just(sample));
        when(airlineRepo.save(any(Airline.class))).thenReturn(Mono.just(updated));
        webTestClient.put()
                .uri("/airlines/AI")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(req)
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.name").isEqualTo("Ai update");
    }

    @Test
    void deleteAirlineSuccess() 
    {
        when(airlineRepo.findById("AI")).thenReturn(Mono.just(sample));
        when(airlineRepo.deleteById("AI")).thenReturn(Mono.empty());
        webTestClient.delete().uri("/airlines/AI").exchange().expectStatus().isOk();
    }


}
