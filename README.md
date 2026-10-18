# URL Shortener

A backend service that converts long URLs into short, non-guessable codes and redirects visitors to the original link. Built with Spring Boot as a learning project that goes beyond basic CRUD: ID obfuscation, caching, and resilience.

## Status
In progress. Core shorten/resolve pipeline, Hashids obfuscation, and Redis caching are done.

## Tech Stack
- Java, Spring Boot, Maven
- Spring Data JPA + PostgreSQL
- Hashids (reversible ID obfuscation)
- Redis (cache-aside), run via Docker

## API

| Method | Endpoint | Description | Success |
|---|---|---|---|
| POST | `/api/shorten` | Body: `{"url": "https://example.com/long/path"}` | `201 Created`, body: `{"shortCode": "vKpAJk"}` |
| GET | `/api/{code}` | Redirects to the original URL | `302 Found` + `Location` header |

### Error handling

| Case | Status |
|---|---|
| Short code can't be decoded (garbage or invalid characters) | `400 Bad Request` |
| Code decodes fine, but no matching row exists | `404 Not Found` |

## How it works

**Shorten**
1. The long URL is saved and Postgres generates an auto-increment id.
2. The id is encoded with **Hashids** (salted, reversible), so sequential ids produce scrambled-looking codes that can't be enumerated by counting up.

**Resolve** (cache-aside)
1. Decode the short code to an id. If it can't be decoded, return 400.
2. Look up `url:<code>` in **Redis**. On a hit, redirect immediately without touching Postgres.
3. On a miss, query Postgres by id (404 if missing), store the URL in Redis with a 24-hour TTL, then redirect.

**Resilience:** Redis is only an optimization. If it is down, cache reads are treated as misses and cache writes are skipped, so redirects still work from Postgres, only slower.

## Design decisions
- **Hashids over plain Base62(id):** my first version (`Base62Encoder.java`, kept in the repo, unused) encoded the raw id, which produced sequential, guessable codes and allowed enumeration of every stored URL. Hashids fixes this.
- **302 instead of 301:** browsers re-request every time, so every click reaches the server, which keeps click analytics possible later.
- **Validation in the service layer, HTTP mapping in a `@ControllerAdvice`:** the service throws `InvalidShortCodeException` / `UrlNotFoundException`; the handler maps them to 400 / 404.
- **DTOs instead of exposing the entity:** prevents mass assignment and keeps the API contract independent of the DB model.
- **Cache only successful lookups:** 404s are never cached.
- **TTL of 24 hours:** bounds memory use and limits how stale an entry can get.

## Known limitations / planned improvements
- `shortCode` is still stored in the DB but `resolve()` uses `findById`, so the column is write-only. Removing it would also remove the second save in `shorten()`.
- `shorten()` performs two saves (insert, then update). A single save becomes possible once the column is removed.
- `spring.jpa.hibernate.ddl-auto=update` is for local development; production would use Flyway or Liquibase migrations.
- Secrets (DB password, salt) currently live in `application.properties`; planned move to environment variables.
- No URL validation on input, no custom aliases, no click analytics yet.

## Setup

**Prerequisites:** Java, Maven, PostgreSQL, Docker

1. Create a Postgres database named `url_shortener_db`.
2. Start Redis:
```
   docker run --name redis-url -p 6379:6379 -d redis:7.2
```
After a restart, use `docker start redis-url`.
3. In `src/main/resources/application.properties`, set:
```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener_db
   spring.datasource.username=postgres
   spring.datasource.password=<your-password>
   hashids.salt=<any-string-you-choose>
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   spring.data.redis.connect-timeout=500ms
   spring.data.redis.timeout=500ms
```
Changing the salt later makes previously generated codes undecodable.
4. Run:
```
   ./mvnw spring-boot:run
```

## Roadmap
- [x] Entity, repository, service layer
- [x] Exception handling (400 / 404)
- [x] REST controller (201 / 302)
- [x] Hashids ID obfuscation
- [x] Redis caching with Postgres fallback
- [ ] Rate limiting (Bucket4j)
- [ ] Dockerize app + Postgres + Redis with Docker Compose
- [ ] Async click analytics
- [ ] Tests (JUnit, Mockito, Testcontainers)
- [ ] CI with GitHub Actions


##Request Path
[ Browser / Insomnia ] ➔ [ Tomcat ] ➔ [ RateLimitFilter ] ➔ [ DispatcherServlet ] ➔ [ UrlController ] ➔ [ UrlService ] ➔ [ Redis / Postgres ]
