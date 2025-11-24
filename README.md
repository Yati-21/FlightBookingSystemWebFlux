# FLIGHT BOOKING SYSTEM

### A fully reactive, non-blocking backend built with Spring Boot WebFlux and Reactive MongoDB for managing flights, bookings, users, and airlines.

[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203%20%2B%20WebFlux-brightgreen)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-Reactive%20MongoDB-4EA94B)](https://www.mongodb.com/)

---

### Table of Contents

* [System Architecture](#system-architecture)
* [Key Features](#key-features)
* [Technical Highlights](#technical-highlights)
* [Documentation](#documentation)

---

### System Architecture

![er diagram](https://github.com/user-attachments/assets/0fafdae6-ac7d-4163-ada8-f9e6b071e7b5)

---

### Key Features

The system offers a comprehensive set of reactive features for both passengers and administrative staff.

#### User Features
* **Flight Search:** Search by origin, destination, and date.
* **Booking:** Ticket booking with passenger list and seat selection.
* **Retrieval:** Fetch booking details using PNR.
* **History:** View booking history by email or user ID.
* **Cancellation:** Booking cancellation (with a business rule restricting cancellation less than 24 hours before departure).

#### Admin & System Features
* **Management:** Full CRUD operations for Airlines, Flights, Users, Bookings & Passengers.
* **Availability:** Automatic seat availability management and seat conflict detection.
* **Validation:** Global error handling and strong business validations (e.g., source and destination cities must differ).
* **Architecture:** Reactive APIs using `Mono` & `Flux`.

---

### Technical Highlights

| Component | Technology | Details |
| :--- | :--- | :--- |
| **Architecture** | `Spring Boot WebFlux` | `Non-blocking, reactive framework` |
| **Database** | `Reactive MongoDB` | `Asynchronous data access` |
| **Quality** | `SonarQube` | `Static code analysis and issue tracking` |
| **Coverage** | `JaCoCo` | `Code coverage reports (91%)` |
| **Load Testing** | `Apache JMeter` | `Performance testing for high concurrency` |

---

### Documentation

For detailed information on APIs, data models, and testing, please refer to the links below:

* **API Testing Documentation:** Detailed screenshots and examples of all API requests and responses.
    [API Testing Screenshots](https://docs.google.com/document/d/1FgRpyqi2NfTODbka28p0oVAvJWYLtPqGo84eZlSM8KY/edit?tab=t.0)

* **Full Project Documentation:** Deep dive into the project's architecture, features, and technology stack.
    [Project Documentation](https://docs.google.com/document/d/1JtnmbO8LRWkaybb6AyGIHzGnAz5JMVgIg6eLKpmvXoc/edit?tab=t.0#heading=h.1o82uf1hjvs5)
