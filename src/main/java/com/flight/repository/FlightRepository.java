package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.AirportCode;
import com.flight.entity.Flight;

import reactor.core.publisher.Flux;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String>
{
    Flux<Flight> findByFromCityAndToCity(AirportCode from,AirportCode to);
}
