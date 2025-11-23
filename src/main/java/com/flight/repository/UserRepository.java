package com.flight.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.entity.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User,String> 
{

}
