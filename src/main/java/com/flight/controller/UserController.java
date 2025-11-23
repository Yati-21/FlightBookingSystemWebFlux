package com.flight.controller;

import com.flight.entity.User;
import com.flight.exception.NotFoundException;
import com.flight.repository.UserRepository;
import com.flight.request.UserUpdateRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController 
{
    @Autowired
    private UserRepository userRepo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody @Valid User user) 
    {
        return userRepo.save(user);
    }

    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable String id) 
    {
        return userRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    @GetMapping
    public Flux<User> getAllUsers() 
    {
        return userRepo.findAll();
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id,@RequestBody @Valid UserUpdateRequest req) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(existing-> 
                {
                    existing.setName(req.getName());
                    existing.setEmail(req.getEmail());
                    return userRepo.save(existing);
                });
    }
    

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable String id) 
    {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(u->userRepo.deleteById(id));
    }
}
