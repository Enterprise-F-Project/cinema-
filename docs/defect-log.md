# Defect log (Parts F)

Course: Software Testing and Validation — Cinema Distribution System  
Group: Enterprise-F-Project

Status values follow the defect lifecycle: **New → Assigned → Fixed → Verified → Closed** (or **Deferred** / **Rejected**).

| ID | Title | Steps to reproduce | Expected | Actual | Severity | Priority | Status | Found by |
|----|-------|--------------------|----------|--------|----------|----------|--------|----------|
| DEF-001 | Public registration allows ADMIN role | `POST /api/auth/register` with `"role":"ADMIN"` | Registration should reject elevated roles | User created with ADMIN | High | P1 | Deferred (documented finding; not “fixed” during test phase) | Phase 4 security tests |
| DEF-002 | Auth failures return HTTP 403 | Call protected API with no/invalid/expired/blacklisted JWT | Prefer **401 Unauthorized** for authentication failures | **403 Forbidden** under current `SecurityConfig` | Medium | P2 | Deferred (behaviour documented by security tests) | Phase 4 |
| DEF-003 | Disabled-account message not reached | Login with an inactive user | Clear “Account is disabled” from AuthService | Spring Security rejects authentication earlier | Low | P3 | Deferred | Phase 4 |
| DEF-004 | Rental UI date format rejected by API | Create rental via UI (`<input type="date">`) | Success with date-only values or UI sends datetime | Backend expects `LocalDateTime`; date-only → 400 “Invalid value for request field” | High | P1 | **Fixed** — frontend appends `T00:00:00` (`rentals-page-content.tsx`) | Phase 5 Selenium |

## Notes

- Severity: impact if released. Priority: order of fixing.
- DEF-001–003 are intentional **test findings** kept visible; production behaviour was not silently changed to force green tests during Phase 4.
- DEF-004 blocked the rental E2E scenario and was fixed so validation (system testing) could complete.
