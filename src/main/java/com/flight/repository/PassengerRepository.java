package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.Passenger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger,String> 
{
    Flux<Passenger> findByBookingId(String bookingId);

    //check if any passenger with seatNumber exists under any of bookingIds
    Mono<Boolean> existsBySeatNumberAndBookingIdIn(String seatNumber,List<String> bookingIds);

    Mono<Void> deleteByBookingId(String bookingId);

}
