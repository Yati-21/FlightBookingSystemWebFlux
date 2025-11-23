package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.Booking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking,String> 
{
	Mono<Booking> findByPnr(String pnr);
    Flux<Booking> findByUserId(String userId);
    Flux<Booking> findByFlightId(String flightId);
    Mono<Boolean> existsByPnr(String pnr);
}
