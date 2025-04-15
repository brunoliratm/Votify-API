# Votify-Api

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
- `POST /api/v1/agendas/{id}/start-voting` - Start voting on an agenda
  - Query Parameters:
    - `durationSeconds` (optional) - Voting duration in seconds

### Votes
- `POST /api/v1/votes` - Register a vote (only for associates)

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

## 📞 Contact
- Email - [exploitexpertsmail@gmail.com](mailto:exploitexpertsmail@gmail.com)
- Project Link: [https://github.com/Exploit-Experts/Votify-Api](https://github.com/Exploit-Experts/Votify-Api)