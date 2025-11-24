package com.flight.controller;

import com.flight.entity.User;
import com.flight.exception.NotFoundException;
import com.flight.repository.UserRepository;
import com.flight.request.UserCreateRequest;
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
	private static final String USER_NOT_FOUND="User not found";
    @Autowired
    private UserRepository userRepo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createUser(@RequestBody @Valid UserCreateRequest req) 
    {
        User user =new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        return userRepo.save(user).map(User::getId);
    }

    @GetMapping
    public Flux<User> getAllUsers() 
    {
    	return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable String id) 
    {
        return userRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)));
    }


    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id,@RequestBody @Valid UserUpdateRequest req) {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                .flatMap(existing-> 
                {
                    existing.setName(req.getName());
                    existing.setEmail(req.getEmail());
                    return userRepo.save(existing);
                });
    }
    

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) 
    {
        return userRepo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                .flatMap(u->userRepo.deleteById(id));
    }
}
