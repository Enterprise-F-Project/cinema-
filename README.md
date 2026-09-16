#Cinema Distribution System

An Enterprise Application developed using **Spring Boot**, **React**, **PostgreSQL**, **Docker**, and **JWT Authentication** for managing movie distribution, clients, rentals, and licenses.

---

## Project Description

The Cinema Distribution System is a web-based enterprise application designed to automate movie distribution management. The system enables administrators to manage movies, clients, rentals, and licenses while providing secure authentication and role-based authorization.

The application follows a layered architecture and communicates through RESTful APIs documented using Swagger OpenAPI.

---

## Features

- User Registration
- User Login with JWT Authentication
- Role-Based Authorization
- Movie Management (CRUD)
- Client Management (CRUD)
- Rental Management (CRUD)
- License Management (CRUD)
- Dashboard
- Pagination & Sorting
- Search & Filtering
- Global Exception Handling
- REST API
- Docker Support
- PostgreSQL Database
- Swagger API Documentation

---

## User Roles

- ADMIN
- DISTRIBUTOR
- CLIENT

Each role has different permissions controlled using Spring Security and JWT.

---

## Technology Stack

### Frontend

- React
- JavaScript
- CSS

### Backend

- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Maven

### Database

- PostgreSQL
- Flyway Migration

### DevOps

- Docker
- Docker Compose

### Tools

- Git
- GitHub
- Postman
- Swagger OpenAPI

---

## 📂 Project Structure

```
Cinema Distribution System
│
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
│
├── backend/
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── docker-compose.yml
│
└── README.md
```

---

## ⚙️ Installation

### 1. Clone Repository

```bash
git clone https://github.com/Enterprise-F-Project/cinema-.git
```

---

### 2. Start PostgreSQL using Docker

```bash
docker compose up -d
```

---

### 3. Run Backend

```bash
cd backend
mvn spring-boot:run
```

## Live Application

Frontend

http://196.189.188.234:3001/

Backend API

http://196.189.188.234:8080/

Swagger UI

http://196.189.188.234:8080/swagger-ui/index.html
```

---

##  Authentication

Authentication is implemented using JSON Web Tokens (JWT).

Public endpoints

```
POST /api/auth/register
POST /api/auth/login
```

All other API endpoints require a valid JWT Bearer Token.

---

## API Documentation

Swagger UI

```
http://196.189.188.234:8080/swagger-ui/index.html
```

OpenAPI JSON

```
http://196.189.188.234:8080/v3/api-docs
```

---

## 🧪 Testing

### Automated tests (Maven)

From the `backend/` directory:

```bash
mvn clean test
```

This runs the full pyramid:

| Level | Suite | Approx. count |
|-------|--------|----------------|
| Unit | Phase 3 service tests (Mockito) | 54+ |
| Validation | Phase 6 controller Bean Validation (MockMvc) | 5 |
| Integration / security | Phase 4 MockMvc + Testcontainers PostgreSQL | 19 |
| System / E2E | Phase 5 Selenium Page Object Model | 3 |

**Requirements for the full suite (including Selenium):**

- Java 21
- Docker Desktop running (Testcontainers + app Postgres)
- Backend on `http://localhost:8080`
- Frontend on `http://localhost:3000`
- Google Chrome (Selenium Manager resolves the driver)
- `backend/src/test/resources/docker-java.properties` sets `api.version=1.44` for Docker Engine 29+

CI pipelines exclude Selenium (no live frontend on the agent). Run Selenium locally:

```bash
mvn -B test -Dtest=CinemaSystemTest -Dselenium.headless=true
```

Coverage report (JaCoCo):

`backend/target/site/jacoco/index.html`

**Target:** ≥ **90%** branch coverage of core business services (`AuthService`, `RentalService`, `MovieService`, `LicenseService`).

### Phase 6 — Validation / BVA / EP

Controller tests under `backend/src/test/java/com/cinema/controller/` demonstrate:

1. Password length 7 → 400 (BVA invalid)
2. Invalid email → 400 (EP)
3. Password length 8 → 201 (BVA valid boundary)
4. Movie duration 0 → 400 (`@Positive`)
5. Null `movieId` on license → 400 (`@NotNull`)

### Manual API testing

API testing was also performed using Postman for registration, login, JWT authentication, movies, clients, rentals, and licenses.

---

## Continuous Integration

### GitHub Actions

Workflow: `.github/workflows/ci.yml`

- Triggers on push / pull request to `main` (and team feature branches).
- Sets up JDK 21 and runs `mvn -B clean test -Dtest='!CinemaSystemTest'` in `backend/`.
- Uploads Surefire XML and JaCoCo HTML as artifacts.
- Failed tests fail the workflow (regression gate).

### Jenkins CI

The repository root contains a `Jenkinsfile`.

- Checks out the repo and runs the same Maven suite (Selenium excluded; needs Docker for Testcontainers).
- Failed tests fail the build; Surefire + JaCoCo are archived.
- Configure Jenkins tool names `JDK21` and `Maven`, or adjust the `tools` block.

Example Jenkins-in-Docker:

```bash
docker run -d --name jenkins-cinema -p 8081:8080 -p 50000:50000 ^
  -v //var/run/docker.sock:/var/run/docker.sock ^
  -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

Then create a Pipeline job pointed at this repository and the root `Jenkinsfile`.

### Regression demonstration

1. Temporarily change a known assertion (e.g. expect HTTP 401 instead of 403 on a security test).
2. Push or run the pipeline → build **fails**.
3. Restore the assertion → build **passes**.

Do not leave a broken commit on `main`.

---

## Docker

The PostgreSQL database runs inside Docker.

Start containers

```bash
docker compose up -d
```

Stop containers

```bash
docker compose down
```

---



---

## 👨‍💻 Team Members

| Name | Responsibility |
|------|----------------|
| Zeru | Entities, Repositories, Services, Docker, Integration, Unit / validation testing, GitHub Actions |
| Mistre | DTOs, Controllers, Selenium system tests |
| Hlina | Spring Security, JWT Authentication, Security tests, Jenkins CI |

---

## 📄 License

This project was developed for educational purposes as part of the Enterprise Application Development / Software Testing and Validation course.

---

## 🤖 AI Usage Declaration

During the development of this project, AI-assisted tools (including ChatGPT) were used to assist with code , debugging, documentation drafting, and development guidance. All generated content was reviewed, modified where necessary, integrated by the project team, and verified through implementation and testing.
