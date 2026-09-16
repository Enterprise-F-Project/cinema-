# Test Plan (Part A) — Cinema Distribution System

**Course:** Software Testing and Validation  
**Group:** Enterprise-F-Project  
**Application:** Cinema Distribution System (Spring Boot + Next.js)

## 1. Scope

**In scope:** Authentication/JWT, movies, licenses, rentals, role-based access, API validation, UI journeys for client/distributor rental workflow, CI pipelines.

**Out of scope:** Payment gateways, third-party streaming, mobile apps, performance/load testing beyond basic CI timing, accessibility audits.

## 2. Approach (levels & techniques)

| Level | Technique / tool | Evidence in repo |
|-------|------------------|------------------|
| Unit | Equivalence / decision logic via Mockito service tests | `*ServiceTest.java` |
| Unit / API | BVA + EP on DTOs | Phase 6 controller validation tests |
| Integration | Security + DB (Testcontainers) | `SecurityIntegrationTest` |
| System | Selenium + Page Object Model | `CinemaSystemTest` + `pages/*` |
| Regression | GitHub Actions + Jenkins | `.github/workflows/ci.yml`, `Jenkinsfile` |

## 3. Entry criteria

- Application builds; Postgres via Docker Compose; demo users seeded.
- JDK 21 / Maven / Node available for local runs.

## 4. Exit criteria

- Automated suites green on CI (unit + validation + security).
- Selenium green on a full local stack.
- Core business service **branch coverage ≥ 90%**.
- Defects logged; critical path defects fixed or explicitly deferred.
- Test summary recommendation recorded.

## 5. Risk-based prioritisation

| Priority | Area | Why |
|----------|------|-----|
| P1 | Auth / roles / JWT | Security defects have high impact |
| P1 | Rental lifecycle | Core business workflow |
| P2 | License ownership rules | Distributor isolation |
| P2 | Input validation | Prevents bad data / 500s |
| P3 | UI cosmetics | Lower business risk |

## 6. Schedule (summary)

Week 1 app + plan/design → Week 2 automation + CI → Week 3 regression demo, defects, summary.

## 7. Roles

| Member | Focus |
|--------|--------|
| Zeru | Domain services, unit/validation tests, coverage, GitHub Actions |
| Hlina | Security, JWT, security integration tests, Jenkinsfile |
| Mistre | Controllers/DTOs, Selenium POM system tests |
| Yabsra (covered by Zeru) | Validation / BVA / EP contribution |

## 8. UAT / acceptance

Manual Postman and role-based UI checks against demo accounts (`admin@cinema.com`, `distributor@cinema.com`, `client@cinema.com` / `Password123!`).
