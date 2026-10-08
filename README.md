# URL Shortener

A backend service that converts long URLs into short, unique codes — built as a learning project to go beyond basic CRUD.

## Status
🚧 In progress — Phase 1 (core shorten/resolve logic)

## Tech Stack
- Java, Spring Boot
- Spring Data JPA + PostgreSQL
- Maven

## How it works
## How it works
- **Shorten:** the long URL is saved, the DB generates an auto-increment id, and that id is encoded
  into a short code using **Hashids** (a salted, reversible encoding). Sequential ids produce
  scrambled-looking codes, so links can't be enumerated by counting up.
- **Resolve:** the short code is decoded back to the id with Hashids, the row is looked up by id,
  and the server responds with `302 Found` and a `Location` header pointing to the original URL.

### Error handling
| Case | Status |
|---|---|
| Short code can't be decoded (garbage / invalid) | `400 Bad Request` |
| Code decodes fine, but no matching row exists | `404 Not Found` |

## Design decisions & known limitations (to be improved)
- - Originally built a hand-written Base62 encoder (`Base62Encoder.java`, kept in the repo as a
    record of the original design). Replaced by Hashids after identifying that raw Base62(id)
    produces sequential, guessable codes (enumeration risk).
- `shortCode` is still stored in the DB, but `resolve()` decodes the code to an id and uses
  `findById`, so the column is currently write-only. Planned cleanup: remove the column, which
  also removes the second save in `shorten()`.
- `shorten()` performs two saves (insert, then update with the code). A single save becomes
  possible once the `shortCode` column is removed.
## Setup
1. Create a Postgres database named `url_shortener_db`
2. Update `src/main/resources/application.properties` with your DB credentials
3. Run via `./mvnw spring-boot:run`

## Roadmap
- [x] Entity, Repository, Service layer (shorten/resolve)
- [x] Exception handling (400/404)
- [x] REST Controller
- [ ] Redis caching for reads
- [ ] Rate limiting
- [ ] Dockerize