# CI regression demonstration (Part E)

Record this sequence in the test summary report with screenshots or log excerpts.

## Option A — GitHub Actions

1. On a throwaway branch, change one known assertion, e.g. in `SecurityIntegrationTest` expect `401` instead of `403` for an unauthenticated call.
2. Push the branch → Actions run → **red** build.
3. Revert the assertion and push → **green** build.
4. Do **not** merge the broken commit to `main`.

## Option B — Local Maven (same idea)

```bash
cd backend
# break one assertion, then:
mvn -B test -Dtest=SecurityIntegrationTest#protectedEndpoint_withoutJwt_isRejected
# restore, then re-run — BUILD SUCCESS
```

## Option C — Jenkins

Use the repository `Jenkinsfile` on an agent with JDK 21, Maven, and Docker. Trigger a build after the deliberate break and after the fix; archive console output.
