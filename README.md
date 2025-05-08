<div align="center" text-align="center">
    <img src="https://capsule-render.vercel.app/api?type=waving&height=200&color=gradient&text=Votify%20API&reversal=false">

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![GitHub pull requests](https://img.shields.io/github/issues-pr/brunoliratm/Votify-API)
![GitHub contributors](https://img.shields.io/github/contributors/brunoliratm/Votify-API)

</div>

## 📋 Overview

Votify-Api is a REST API developed with Spring Boot for managing voting systems in cooperatives. The platform allows creating voting sessions, managing agendas, and controlling the voting process by associates.

## ✨ Features

- Authentication and authorization with different access levels (ADMIN, ORGANIZER, ASSOCIATE)
- Creation and management of voting sessions with defined time periods
- Management of agendas within sessions
- Secure voting system (Yes/No) with user validation
- Logical deletion (soft delete) for historical data preservation
- Robust exception handling and comprehensive validations
- Integrated API documentation via Swagger/OpenAPI

## 🚀 Installation

### Prerequisites

- Java 21
- PostgreSQL
- Maven 3.9+

### Setup

1. Clone the repository

```bash
git clone https://github.com/Exploit-Experts/Votify-Api.git
cd Votify-Api
```

2. Configure environment variables

```
POSTGRES_URL=localhost:5432/votify
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_password
API_BASEURL=http://localhost:8080/api/v1
ADMIN_DEFAULT_EMAIL=admin@votify.com
ADMIN_DEFAULT_PASSWORD=admin123@
```

3. Build the project

```bash
mvn clean install
```

4. Run the application

```bash
mvn spring-boot:run
# or
java -jar target/votify-0.0.1-SNAPSHOT.jar
```

## 🔌 API Endpoints

### Authentication

- `POST /api/v1/auth/login` - User authentication
- `POST /api/v1/auth/forgot-password` - Request password reset
- `POST /api/v1/auth/reset-password` - Reset password with received code

### Users

- `POST /api/v1/users` - Create a new user
- `GET /api/v1/users` - List users
  - Query Parameters:
    - `page` (optional, default: 1) - Page number
    - `name` (optional) - Filter by name
    - `role` (optional) - Filter by role (ADMIN, ORGANIZER, ASSOCIATE)
- `GET /api/v1/users/{id}` - Find user by ID
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (soft delete)

### Sessions

- `POST /api/v1/sessions` - Create a new session
- `GET /api/v1/sessions` - List all sessions
  - Query Parameters:
    - `page` (optional, default: 1) - Page number
    - `sort` (optional, default: id) - Field to sort
    - `direction` (optional, default: ASC) - Sort direction (ASC, DESC)
- `GET /api/v1/sessions/{id}` - Find session by ID
- `PUT /api/v1/sessions/{id}` - Update session
- `DELETE /api/v1/sessions/{id}` - Delete session (soft delete)

### Agendas

- `POST /api/v1/agendas` - Create a new agenda
- `GET /api/v1/agendas` - List all agendas
  - Query Parameters:
    - `page` (optional, default: 1) - Page number
    - `sort` (optional, default: id) - Field to sort
    - `direction` (optional, default: ASC) - Sort direction (ASC, DESC)
- `GET /api/v1/agendas/{id}` - Find agenda by ID
- `PUT /api/v1/agendas/{id}` - Update agenda
- `DELETE /api/v1/agendas/{id}` - Delete agenda (soft delete)
- `PATCH /api/v1/agendas/{id}/start-voting` - Start voting on an agenda
  - Query Parameters:
    - `durationSeconds` (optional) - Voting duration in seconds
- `PATCH /api/v1/agendas/{id}/stop-voting` - Stop voting on an agenda

### Votes

- `POST /api/v1/votes` - Register a vote (only for associates)
- `PATCH /api/v1/votes` - Update an existing vote (only for associates)

## 🛠️ Technologies

- Java 21
- Spring Boot 3.3.5
- Spring Security with JWT
- Spring Data JPA
- Spring Mail for email sending
- Hibernate/JPA
- PostgreSQL
- Lombok
- BCrypt for password encryption
- Swagger/OpenAPI for documentation
- Maven
- Docker & Docker Compose

## 🐳 Docker Support

The application includes Docker support for easy deployment in any environment. The Dockerfile sets up the appropriate Java runtime environment and configures the application for production use.

### Dockerfile Features

- Multi-stage build for optimized image size
- Maven build in the first stage with Eclipse Temurin 21 Alpine
- Slim JRE-based Alpine runtime image in the second stage
- Proper layer caching for faster builds
- Exposes port 8080 for the application
- Includes curl for health check capabilities

### Environment Variables

The application supports configuration through environment variables, which can be passed when running the Docker container. Spring Boot will automatically map these environment variables to application properties.

Common environment variables you can configure:

- `POSTGRES_URL`: Database URL (e.g., localhost:5432/votify)
- `POSTGRES_USER`: Database username
- `POSTGRES_PASSWORD`: Database password
- `JWT_SECRET`: Secret key used for JWT token signing
- `MAIL_USERNAME`: Email address for sending notifications
- `MAIL_PASSWORD`: Password for the email account
- `API_BASEURL`: Base URL for the API (defaults to http://localhost:8080/api/v1)
- `ADMIN_DEFAULT_EMAIL`: Email for the default admin user (default: admin@votify.com)
- `ADMIN_DEFAULT_PASSWORD`: Password for the default admin user (default: admin123@)
- `SPRING_PROFILES_ACTIVE`: Set to your desired Spring profile (default, dev, prod)
- `SERVER_PORT`: The port on which the application runs (default: 8080)

When running in a Docker container, use `host.docker.internal` to connect to a database running on your host machine, for example:

```bash
docker run --add-host=host.docker.internal:host-gateway \
  -e POSTGRES_URL=host.docker.internal:5432/votify \
  votify-api
```

### Docker Run Examples

#### Basic run with default settings:

```bash
docker build -t votify-api .
docker run -p 8080:8080 votify-api
```

#### Run with environment variables from a file:

```bash
docker run -p 8080:8080 --env-file .env votify-api
```

#### Run with custom environment variables:

```bash
docker run -p 8080:8080 \
  -e POSTGRES_URL=jdbc:postgresql://127.0.0.1:5432/votify \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -e JWT_SECRET=your_secure_jwt_secret \
  -e ADMIN_DEFAULT_EMAIL=admin@votify.com \
  -e ADMIN_DEFAULT_PASSWORD=secure_admin_password \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e API_BASEURL=http://localhost:8080/api/v1 \
  -e MAIL_USERNAME=your_email@gmail.com \
  -e MAIL_PASSWORD=your_email_password \
  votify-api
```

### Docker Compose

For a complete development environment with both the API and PostgreSQL database, use Docker Compose:

```bash
docker compose up -d
```

This will start both the Votify API and a PostgreSQL database with the appropriate configurations.

#### Customizing Docker Compose Environment

You can customize the environment variables in Docker Compose using several approaches:

1. **Using a .env file:**

   Create a `.env` file in the same directory as your `docker-compose.yml`:

   ```bash
   # .env file
   POSTGRES_USER=customuser
   POSTGRES_PASSWORD=secretpassword
   JWT_SECRET=your_custom_secret
   ADMIN_DEFAULT_EMAIL=custom@votify.com
   ADMIN_DEFAULT_PASSWORD=custom_password
   ```

   Docker Compose will automatically read this file and use these values.

2. **Command line environment variables:**

   ```bash
   POSTGRES_PASSWORD=strongpassword JWT_SECRET=supersecret docker compose up -d
   ```

3. **Using the --env-file flag:**

   ```bash
   docker compose --env-file ./custom.env up -d
   ```

4. **Modifying `docker-compose.yml` directly:**

   Edit the environment variables directly in the `docker-compose.yml` file.

These methods allow you to customize the environment for both development and production scenarios while keeping sensitive credentials out of version control.

## 🔒 Security

- Authentication via JWT (JSON Web Tokens)
- Role-based access control (ADMIN, ORGANIZER, ASSOCIATE)
- Passwords encrypted with BCrypt
- Protection against common attacks
- Data validation on all requests

## 📚 Documentation

The API documentation is available at `http://localhost:8080/swagger-ui/index.html` when the application is running.

## 📐 Architecture

The application follows a layered architecture:

- **Controllers**: Handling HTTP requests and responses
- **Facades**: Intermediaries between controllers and services
- **Services**: Business logic
- **Repositories**: Data access
- **Models**: Domain entities
- **DTOs**: Data transfer objects

It also implements several design patterns such as:

- Facade
- Repository
- DTO
- Service Layer
- Soft Delete

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
