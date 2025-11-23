package com.flight.controller;

import com.flight.entity.Airline;
import com.flight.exception.NotFoundException;
import com.flight.repository.AirlineRepository;
import com.flight.request.AirlineCreateRequest;
import com.flight.request.AirlineUpdateRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/airlines")
public class AirlineController 
{

    @Autowired
    private AirlineRepository airlineRepo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Airline> createAirline(@RequestBody @Valid AirlineCreateRequest req) 
    {
        Airline airline=new Airline();
        airline.setCode(req.getCode());
        airline.setName(req.getName());
        return airlineRepo.save(airline);
    }

    @GetMapping
    public Flux<Airline> getAllAirlines() 
    {
        return airlineRepo.findAll();
    }

    @PutMapping("/{code}")
    public Mono<Airline> updateAirline(@PathVariable String code,@RequestBody @Valid AirlineUpdateRequest req) 
    {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .flatMap(existing-> 
                {
                    existing.setName(req.getName());
                    return airlineRepo.save(existing);
                });
    }


    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAirline(@PathVariable String code) 
    {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .flatMap(existing->airlineRepo.deleteById(code));
    }
}
