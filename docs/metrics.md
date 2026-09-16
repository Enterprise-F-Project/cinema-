# Metrics (Part G)

Computed for the Cinema Distribution System testing effort. Update numbers if the suite grows.

## Coverage (JaCoCo)

| Metric | Value | Interpretation |
|--------|-------|----------------|
| Core business services branch coverage (`AuthService`, `RentalService`, `MovieService`, `LicenseService`) | **97.3%** (71/73 branches; JaCoCo) | Exceeds the instructor **90%** target and the PDF **80%** target. |
| Overall project branch coverage | Lower than core (often ~60%) | Expected: UI controllers and exception handlers are exercised less by unit tests; E2E covers journeys instead. |

## Defect metrics

| Metric | Formula / count | Value | Interpretation |
|--------|-----------------|-------|----------------|
| Defects found in testing | Count of log entries | 4 | Includes security findings and one UI/API contract bug. |
| Defects fixed before “release” recommendation | Fixed status | 1 (DEF-004) | Critical path for rentals repaired. |
| Defects deferred / accepted risk | Deferred | 3 (DEF-001–003) | Documented; product owner / team can harden auth later. |
| Defect density (approx.) | defects / KLOC (estimate) | Low single-digit on a small codebase | Useful as a relative indicator, not an industry absolute. |
| Defect removal efficiency (DRE) | found / (found + escaped) | High if no escaped production defects known | No escaped field defects measured in this academic project; treat as “all known defects logged”. |
| Escaped defects | Found after “done” without tests catching | 0 known | Selenium + security suite would have missed DEF-004 until E2E ran; now covered. |

## Test suite size (approximate)

| Suite | Count |
|-------|-------|
| Unit (services) | ~61 |
| Validation (controllers) | 5 |
| Security integration | 19 |
| Selenium system | 3 |
| **Total (full local)** | **~88** |
| **CI suite (no Selenium)** | **85** |

CI (GitHub Actions / Jenkins) runs all except Selenium unless a full UI stack is provisioned on the agent.
