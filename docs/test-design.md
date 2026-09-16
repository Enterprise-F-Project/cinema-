# Test Design (Part B) — Cinema Distribution System

**Course:** Software Testing and Validation  
**Group:** Enterprise-F-Project

## Group members

| Name | Student ID |
|------|------------|
| Zerubabel Mesfin | ATE/0211/14 |
| Hlina Girum | ATE/3417/14 |
| Mistre Solomon | ATE/2545/14 |
| Yabsra Mesfin | ATE/1814/14 |

## 1. Equivalence Partitioning — registration email

| Partition | Example | Expected |
|-----------|---------|----------|
| Valid email | `validation@example.com` | Accepted by Bean Validation |
| Invalid email | `not-an-email` | HTTP 400 |
| Blank email | `""` | HTTP 400 (@NotBlank) |

**Automated:** `register_withInvalidEmail_shouldReturnBadRequest`

## 2. Equivalence Partitioning — license `movieId`

| Partition | Example | Expected |
|-----------|---------|----------|
| Non-null id | `1` | Passes `@NotNull` |
| Null | `null` | 400 |

**Automated:** `createLicense_withNullMovieId_shouldReturnBadRequest`

## 3. Boundary Value Analysis — password length (`@Size(min=8)`)

| Value | Length | Class | Expected |
|-------|--------|-------|----------|
| `Pass123` | 7 | Invalid boundary | 400 |
| `Pass1234` | 8 | Valid min boundary | 201 (with mocked service) |

**Automated:** Phase 6 auth validation tests.

## 4. Boundary Value Analysis — movie duration (`@Positive`)

| Value | Class | Expected |
|-------|-------|----------|
| 0 | Invalid (not > 0) | 400 |
| 1 | Min valid | Would pass validation |

**Automated:** `createMovie_withZeroDuration_shouldReturnBadRequest`

## 5. Decision table — who may create a license

| Condition | R1 | R2 | R3 | R4 |
|-----------|----|----|----|----|
| Role = DISTRIBUTOR | Y | Y | N | N |
| Role = ADMIN | N | N | Y | N |
| Movie owned by caller | Y | N | — | — |
| **Action: create allowed** | Y | N | Y | N |

**Covered by:** `LicenseServiceTest` create success / forbidden / admin / client paths.

## 6. State transition — rental status

Valid:
```
REQUESTED → ACTIVE → COMPLETED
REQUESTED → COMPLETED
```

Invalid examples: `ACTIVE → REQUESTED`, `COMPLETED → ACTIVE`, `REQUESTED → REQUESTED`.

**Covered by:** `RentalServiceTest` transition tests + Selenium rental lifecycle E2E.

See PDF: `docs/Part_B_Test_Design.pdf`
