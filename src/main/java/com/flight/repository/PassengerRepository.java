package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.Passenger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger,String> 
{
	//find all passengers of a particular booking
    Flux<Passenger> findByBookingId(String bookingId);

    Mono<Void> deleteByBookingId(String bookingId);
}
