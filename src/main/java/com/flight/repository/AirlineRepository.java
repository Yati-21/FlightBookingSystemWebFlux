package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.Airline;

@Repository
public interface AirlineRepository extends ReactiveMongoRepository<Airline,String> {
}
