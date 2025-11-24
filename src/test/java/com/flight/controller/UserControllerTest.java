package com.flight.controller;

import com.flight.entity.User;
import com.flight.repository.UserRepository;
import com.flight.request.UserCreateRequest;
import com.flight.request.UserUpdateRequest;
import com.flight.exception.GlobalErrorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@org.springframework.boot.webflux.test.autoconfigure.WebFluxTest(controllers=UserController.class)
@Import(GlobalErrorHandler.class)
class UserControllerTest 
{

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserRepository userRepo;

    private User sampleUser;

    @BeforeEach
    void setup() 
    {
        sampleUser =new User();
        sampleUser.setId("user1");
        sampleUser.setName("abc");
        sampleUser.setEmail("abc@test.com");
    }

    @Test
    void createUserSuccess() 
    {
        UserCreateRequest req=new UserCreateRequest();
        req.setName("abc" );
        req.setEmail("abc@test.com");
        when(userRepo.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON).bodyValue(req)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.id").isEqualTo("user1").jsonPath("$.name").isEqualTo("abc");
    }

    @Test
    void getUserSuccess() 
    {
        when(userRepo.findById("user1")).thenReturn(Mono.just(sampleUser));

        webTestClient.get().uri("/users/user1").exchange().expectStatus().isOk().expectBody()
                .jsonPath( "$.id").isEqualTo("user1").jsonPath("$.email").isEqualTo("abc@test.com");
    }


    @Test
    void updateUserSuccess() 
    {
        UserUpdateRequest req=new UserUpdateRequest();
        req.setName("abc update");
        req.setEmail("abc2@test.com");

        User update=new User("user1", req.getName(),req.getEmail());

        when(userRepo.findById("user1")).thenReturn(Mono.just(sampleUser));
        when(userRepo.save(any(User.class))).thenReturn(Mono.just(update));

        webTestClient.put().uri("/users/user1").contentType(MediaType.APPLICATION_JSON).bodyValue(req)
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.name").isEqualTo("abc update").jsonPath("$.email").isEqualTo("abc2@test.com");
    }


    @Test
    void deleteUserSuccess() 
    {
        when(userRepo.findById("user1")).thenReturn(Mono.just(sampleUser));
        when(userRepo.deleteById("user1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/users/user1").exchange().expectStatus().isNoContent();
    }

}
