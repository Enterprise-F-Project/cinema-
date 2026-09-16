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

- Next.js / React
- TypeScript
- CSS

### Backend

- Spring Boot 3.5 / Java 21
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Maven
- JaCoCo

### Database

- PostgreSQL
- Flyway Migration

### DevOps / CI

- Docker / Docker Compose
- GitHub Actions (`.github/workflows/ci.yml`)
- Jenkins (`Jenkinsfile` + optional Jenkins-in-Docker)

### Tools

- Git / GitHub
- Postman
- Swagger OpenAPI
- Selenium + Chrome (system tests)

---

## How to run the application

### Prerequisites

- Java 21
- Maven
- Node.js 20+
- Docker Desktop

### 1. Clone the repository

```bash
git clone https://github.com/Enterprise-F-Project/cinema-.git
cd cinema-
```

### 2. Start PostgreSQL (Docker)

```bash
cd backend
docker compose up -d
```

Postgres is published on host port **5433** (`cinema-postgres`).

### 3. Run the backend

```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8080  
- Swagger UI: http://localhost:8080/swagger-ui/index.html  

If you see stale Lombok builder compile errors under OneDrive, use:

```bash
mvn clean spring-boot:run -DskipTests
```

If port 8080 is already in use, an older backend may already be running — check http://localhost:8080/actuator/health before starting another instance.

### 4. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

- UI: http://localhost:3000  

### Optional hosted demo URLs

- Frontend: http://196.189.188.234:3001/  
- Backend: http://196.189.188.234:8080/  
- Swagger: http://196.189.188.234:8080/swagger-ui/index.html  

---

## Authentication

Authentication uses JSON Web Tokens (JWT).

Public endpoints:

```
POST /api/auth/register
POST /api/auth/login
```

All other API endpoints require a valid JWT Bearer token.

---

## How to run the tests

All automated tests live under `backend/src/test/java`. From `backend/`:

### Full local suite (unit + validation + security + Selenium)

```bash
mvn clean test
```

| Level | Suite | Approx. count |
|-------|--------|----------------|
| Unit | Service tests (Mockito) | ~61 |
| Validation | Controller Bean Validation / BVA–EP | 5 |
| Integration / security | MockMvc + Testcontainers PostgreSQL | 19 |
| System / E2E | Selenium Page Object Model | 3 |

**Full suite (including Selenium) needs:** Java 21, Docker Desktop, backend on `http://localhost:8080`, frontend on `http://localhost:3000`, Google Chrome.  
`backend/src/test/resources/docker-java.properties` sets `api.version=1.44` for Docker Engine 29+.

### Run suites separately (PowerShell: quote `-Dtest=...`)

```powershell
mvn test "-Dtest=LicenseServiceTest,MovieServiceTest,RentalServiceTest,AuthServiceTest"
mvn test "-Dtest=SecurityIntegrationTest"
mvn test "-Dtest=AuthControllerValidationTest,CreateRequestDtoValidationTest"
mvn test "-Dtest=CinemaSystemTest"
```

CI-style suite (no Selenium):

```powershell
mvn test "-Dtest=!CinemaSystemTest"
```

### Coverage (JaCoCo)

After tests:

`backend/target/site/jacoco/index.html`

**Target:** ≥ **90%** branch coverage of core business services (`AuthService`, `RentalService`, `MovieService`, `LicenseService`).

### Phase 6 — Validation / BVA / EP examples

1. Password length 7 → 400 (BVA invalid)  
2. Invalid email → 400 (EP)  
3. Password length 8 → 201 (BVA valid boundary)  
4. Movie duration 0 → 400 (`@Positive`)  
5. Null `movieId` on license → 400 (`@NotNull`)  

Manual API checks were also done with Postman (register, login, JWT, movies, clients, rentals, licenses).

---

## How to run the CI pipelines

### GitHub Actions

- Config: `.github/workflows/ci.yml`  
- Triggers: push / pull request to `main` (and team feature branches).  
- Steps: JDK 21 → `mvn -B clean test -Dtest='!CinemaSystemTest'` in `backend/` → upload Surefire + JaCoCo artifacts.  
- Failed tests fail the workflow (regression gate).  
- View runs: GitHub → **Actions** tab for this repository.

### Jenkins

- Config: root `Jenkinsfile`  
- Same Maven suite as Actions (Selenium excluded); needs Docker for Testcontainers.  
- Configure Jenkins tools named `JDK21` and `Maven`, or edit the `tools` block.  
- Failed tests fail the build; Surefire + JaCoCo are archived.

Example Jenkins-in-Docker:

```bash
docker run -d --name jenkins-cinema -p 8081:8080 -p 50000:50000 ^
  -v //var/run/docker.sock:/var/run/docker.sock ^
  -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

Then create a **Pipeline** job pointed at this repository and the root `Jenkinsfile`.

### Regression demonstration

1. Temporarily change a known assertion (e.g. expect HTTP 401 instead of 403 on a security test).  
2. Push or run the pipeline → build **fails**.  
3. Restore the assertion → build **passes**.  

Do not leave a broken commit on `main`.

---

## Project structure

```
cinema-/
├── frontend/                 # Next.js UI
├── backend/                  # Spring Boot API + tests
│   ├── src/main/
│   ├── src/test/             # unit, validation, security, Selenium
│   ├── docker-compose.yml    # PostgreSQL
│   └── pom.xml
├── docs/                     # test plan, defects, metrics, summaries
├── .github/workflows/ci.yml  # GitHub Actions
├── Jenkinsfile               # Jenkins pipeline
└── README.md
```

---

## Docker

```bash
cd backend
docker compose up -d
docker compose down
```

---

## Team Members

| Name | ID | Responsibility |
|------|----|----------------|
| Zeru | ATE/0211/14 | Entities, repositories, services, Docker, unit / validation testing, GitHub Actions |
| Mistre | ATE/2545/14 | DTOs, controllers, Selenium system tests |
| Hlina | ATE/3417/14 | Spring Security, JWT, security tests, Jenkins CI |
| Yabsra | ATE/1814/14 | Team member / project support |

---

## 📄 License

This project was developed for educational purposes as part of the Enterprise Application Development / Software Testing and Validation course.

---

## 🤖 AI Usage Declaration

During the development of this project, AI-assisted tools (including ChatGPT) were used to assist with code , debugging, documentation drafting, and development guidance. All generated content was reviewed, modified where necessary, integrated by the project team, and verified through implementation and testing.
