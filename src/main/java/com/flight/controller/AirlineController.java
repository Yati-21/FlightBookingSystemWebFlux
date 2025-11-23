package com.flight.controller;

import com.flight.entity.Airline;
import com.flight.exception.NotFoundException;
import com.flight.repository.AirlineRepository;
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
    public Mono<Airline> createAirline(@RequestBody @Valid Airline airline) 
    {
        return airlineRepo.save(airline);
    }

    @GetMapping("/{code}")
    public Mono<Airline> getAirline(@PathVariable String code) 
    {
        return airlineRepo.findById(code).switchIfEmpty(Mono.error(new NotFoundException("Airline not found")));
    }

    @GetMapping
    public Flux<Airline> getAllAirlines() 
    {
        return airlineRepo.findAll();
    }

    @PutMapping("/{code}")
    public Mono<Airline> updateAirline(@PathVariable String code, @RequestBody @Valid Airline airline) 
    {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .flatMap(existing-> 
                {
                    existing.setName(airline.getName());
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
