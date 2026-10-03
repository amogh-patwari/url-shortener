# URL Shortener

A backend service that converts long URLs into short, unique codes — built as a learning project to go beyond basic CRUD.

## Status
🚧 In progress — Phase 1 (core shorten/resolve logic)

## Tech Stack
- Java, Spring Boot
- Spring Data JPA + PostgreSQL
- Maven

## How it works
- Each URL is saved with an auto-incrementing DB id
- That id is encoded into a short, unique code using **Base62 encoding** (62 symbols: 0-9, a-z, A-Z)
- Reversible: short code → decoded back to id → DB lookup → original URL

## Design decisions & known limitations (to be improved)
- Currently uses plain Base62(id) — codes are sequential/guessable. Planning to add ID scrambling (Hashids-style) to prevent enumeration attacks.
- `shorten()` currently performs two DB saves (insert, then update with the code). A Postgres SEQUENCE-based approach could reduce this to one atomic insert.
- `spring.jpa.hibernate.ddl-auto=update` is used for local development convenience; a production setup would use Flyway/Liquibase migrations instead.

## Setup
1. Create a Postgres database named `url_shortener_db`
2. Update `src/main/resources/application.properties` with your DB credentials
3. Run via `./mvnw spring-boot:run`

## Roadmap
- [x] Entity, Repository, Service layer (shorten/resolve)
- [ ] Exception handling (400/404)
- [ ] REST Controller
- [ ] Redis caching for reads
- [ ] Rate limiting
- [ ] Dockerize