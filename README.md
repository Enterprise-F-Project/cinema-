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

API testing was performed using Postman.

Tested modules include:

- Registration
- Login
- JWT Authentication
- Movies
- Clients
- Rentals
- Licenses

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
| Zeru | Entities, Repositories, Services, Docker, Integration, Testing |
| Mistre | DTOs, Controllers |
| Hlina | Spring Security, JWT Authentication |

---

## 📄 License

This project was developed for educational purposes as part of the Enterprise Application Development course.

---

## 🤖 AI Usage Declaration

During the development of this project, AI-assisted tools (including ChatGPT) were used to assist with code , debugging, documentation drafting, and development guidance. All generated content was reviewed, modified where necessary, integrated by the project team, and verified through implementation and testing.
