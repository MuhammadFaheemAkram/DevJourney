# Testing

DevJourney favors fast local tests for domain logic and repository-facing use cases. The current test suite runs with JUnit and fake repositories.

## Run Tests

```bash
./gradlew :app:testDebugUnitTest
```

## Run Lint

```bash
./gradlew :app:lintDebug
```

## Current Coverage

- Roadmap completion calculations.
- Learning streak calculations.
- Goal progress calculations.
- Notes create, update, delete, and search use cases.
- Goals observation and progress updates.
- Challenge completion and solution note normalization.
- Resource search and bookmarking.
- Global search grouping.
- Analytics observation and streak use cases.
- Settings update and observation use cases.

## Test Strategy

Use fast unit tests when behavior is pure or repository contracts can be faked. Prefer fake repositories over mocks unless call verification is the point of the test.

Good unit test targets:

- Use cases.
- Pure calculation helpers.
- UI state reducers.
- Mapper behavior.
- Search and filtering logic.

Good instrumented test targets:

- Room DAOs.
- Database migrations.
- Compose UI flows.
- WorkManager scheduling integration.

## Future Test Work

- Add Turbine for more expressive Flow tests.
- Add Room DAO tests for notes, progress, bookmarks, goals, challenges, and resources.
- Add Compose UI tests for roadmap list, topic completion, notes CRUD, search, settings toggles, and analytics rendering.
- Add migration tests when the database version changes.
- Add screenshot tests or visual QA docs for major design system changes.

## Pull Request Expectations

For behavior changes, include a local verification summary:

```text
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

For UI changes, include screenshots or a short screen recording in the pull request.
