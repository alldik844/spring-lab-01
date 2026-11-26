# Laboratory Work 12 — Evidence

## Project Information

Project: spring-lab-01
Branch: lab12
Variant: 12 — NotificationService
Framework: Spring Boot 4.0.8
Java: 25

## Automated Test Results

Command: ./mvnw clean verify

Tests run: 27
Failures: 0
Errors: 0
Skipped: 0

## Completed Test Areas

1. NotificationService unit tests — JUnit 5 and Mockito
2. Web layer tests — MockMvc
3. Repository tests — DataJpaTest
4. PostgreSQL tests — Testcontainers
5. Notification spy test — MockitoSpyBean

## JaCoCo Coverage

Package: kz.iitu.springlab.notify

Before additional test:
- Line coverage: [fill from screenshot 6]
- Branch coverage: [fill from screenshot 6]

After additional test:
- Line coverage: 63.04%
- Branch coverage: 75.00%

Additional test:
enabledNotificationsAreSentSuccessfully

## Coverage Evidence

Screenshot 6: JaCoCo before the additional test.
Screenshot 7: JaCoCo after the additional test.

## Remaining Verification

- PostgreSQL integration tests run through Testcontainers.
- An authenticated MockMvc create/read test was added.
- Repository tests use TestEntityManager with positive and negative cases.
- The repository under test is AppUserRepository rather than BookRepository.
- The integration HTTP test uses MockMvc rather than RestTestClient.

## Conclusion

Unit, web, repository, and PostgreSQL tests were
executed. JaCoCo was used to measure test coverage.
The final result is recorded above.
