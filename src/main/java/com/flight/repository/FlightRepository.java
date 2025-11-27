package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.AIRPORT_CODE;
import com.flight.entity.Flight;

import reactor.core.publisher.Flux;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String>
{
    Flux<Flight> findByFromCityAndToCity(AIRPORT_CODE from,AIRPORT_CODE to);
    Flux<Flight> findByAirlineCode(String airlineCode);
}
