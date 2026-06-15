# WA Test Assignment

Spring Boot REST API for the WA Technology Java assignment. The application exposes Star Wars character endpoints backed by a configurable SWAPI-compatible API, plus a simple mocked authentication flow with in-memory tokens.

## Requirements

- Java 21+
- Maven

## Run

```bash
mvn spring-boot:run
```

The application starts on the default Spring Boot port: `http://localhost:7676`.

## Run Tests

```bash
mvn test
```

## Configuration

SWAPI base URL is configured in `src/main/resources/application.properties`:

```properties
swapi.base-url=https://swapi.info/api
```

`swapi.info` is used by default because the original `swapi.dev` API can have SSL certificate issues in some environments. The URL is configurable, so another SWAPI-compatible API can be used without code changes.

## Endpoints

### People

```http
GET /people?page=1
```

Returns a paginated list of Star Wars characters.

```http
GET /people/1
```

Returns character details with these business fields:

```json
{
  "name": "Boba Fetta",
  "height": 1.72,
  "mass": 77.0,
  "birth_year": "19BBY",
  "number_of_films": 4,
  "date_added": "09-12-2014"
}
```

Invalid page or id values such as `0` or negative numbers return `400 Bad Request`. Missing characters return `404 Not Found`.

### Authentication

```http
POST /auth/login
Content-Type: application/json

{
  "username": "test",
  "password": "password"
}
```

Returns:

```json
{
  "accessToken": "1acd1211-812c-44c3-a7af-6f8025b95dad",
  "refreshToken": "21a4411b-f921-42cc-9389-b98dec56da80",
  "user": {
    "username": "test"
  }
}
```

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "21a4411b-f921-42cc-9389-b98dec56da80"
}
```

Returns:

```json
{
  "accessToken": "21a4411b-f921-42cc-9389-b98dec56da80"
}
```

```http
POST /auth/logout
Authorization: Bearer <accessToken>
```

Returns `204 No Content` and invalidates the current access token.

### Favourites

```http
GET /favourites
Authorization: Bearer <accessToken>
```

Returns a static list of favorite characters. Calling this endpoint without a valid Bearer token returns `401 Unauthorized`.

## Example Auth Flow

1. Call `POST /auth/login` with any username and password.
2. Copy the returned `accessToken`.
3. Call `GET /favourites` with `Authorization: Bearer <accessToken>`.
4. If a new access token is needed, call `POST /auth/refresh` with the `refreshToken`.
5. Call `POST /auth/logout` with the current access token to invalidate it.

Authentication is mocked for assignment purposes. Access tokens and refresh tokens are generated UUID values stored in memory.

## Design Decisions and Trade-offs

- In-memory cache is implemented with `ConcurrentHashMap` in `PeopleService`; Redis or another external cache would be unnecessary for this assignment scope.
- Authentication is mocked and token storage is in memory, as required by the assignment.
- No database is used because there is no persistence requirement.
- SWAPI base URL is configurable through `application.properties`.
- Error handling is centralized in `GlobalExceptionHandler` so expected user and external API errors return clear HTTP statuses instead of raw `500` responses.