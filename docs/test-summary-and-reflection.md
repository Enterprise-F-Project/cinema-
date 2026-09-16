# Test Summary Report & Foundations Reflection (Parts H & I)

## What was tested

- Unit tests of Auth, Movie, License, Rental services (mocks/test doubles).
- Controller Bean Validation (BVA/EP).
- Security integration (JWT, roles, blacklist, disabled user behaviour).
- Selenium system tests (login, movie create, rental lifecycle) with Page Objects.
- CI: GitHub Actions + Jenkinsfile (Maven suite excluding Selenium on agents without UI).

## What was not tested (residual)

- Full load/performance; mobile clients; exhaustive UI visual regression.
- Changing production SecurityConfig to return 401 (deferred defect).
- Blocking ADMIN self-registration (deferred defect).

## Results vs exit criteria

| Criterion | Result |
|-----------|--------|
| Automated CI suite green | Yes (unit + validation + security) |
| Selenium on full stack | Verified on developer machine (3/3) |
| Core branch coverage ≥ 90% | **Yes — 97.3%** (71/73) |
| Critical rental path | Fixed (DEF-004) |

## Outstanding defects / residual risk

See `docs/defect-log.md`. Deferred auth findings (DEF-001–003) remain accepted risk for this academic release unless the team hardens security later.

## Recommendation

**Ready for academic release / demonstration**, with documented deferred security findings. Not recommended as a production internet deployment without addressing DEF-001–003.

---

## Foundations reflection (Part I) — half page

**Defect chosen: DEF-004 (rental date format).**

- **Error (human mistake):** The API contract used `LocalDateTime` while the UI collected calendar dates only (`YYYY-MM-DD`), without converting before `POST /api/rentals`.
- **Fault (defect in code):** Mismatch between frontend payload and `CreateRentalRequest` types so Jackson rejected the body.
- **Failure (observed behaviour):** Users (and Selenium) saw HTTP 400 “Invalid value for request field”; the rental sheet stayed open.

**Verification vs validation:** Unit tests of `RentalService` (verification against a correct DTO) would not catch this UI contract bug. It was found by **validation**-style system testing (Selenium exercising the real UI → API path) and confirmed with direct API calls (date-only fails; `…T00:00:00` succeeds). Fixing the UI conversion removed the failure mode.
