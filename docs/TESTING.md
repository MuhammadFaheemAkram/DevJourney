# Testing

DevJourney currently has a focused local unit-test suite. This document separates what exists from dependencies or test types that are only prepared for future use.

## Current Setup

- JUnit 4 runs local JVM tests under `app/src/test`.
- Coroutines are exercised with `runBlocking` where suspend use cases are tested.
- Fake repositories use `MutableStateFlow` to validate observable use-case behavior.
- AndroidX JUnit, Espresso, and Compose UI test dependencies are declared, but there are currently no files under `app/src/androidTest`.
- Turbine and MockK are not currently dependencies.

## Existing Unit Tests

### Calculation Tests

`ProgressCalculationsTest` covers:

- Roadmap completion percentage.
- Consecutive-day learning streaks.
- Average goal completion rate.
- Goal progress clamping.

### Use-Case Tests

- `NoteUseCasesTest`: observe, create, update, delete, and search notes.
- `GoalUseCasesTest`: observe goals, clamp progress updates, and calculate average progress.
- `ChallengeUseCasesTest`: complete challenges and normalize solution notes.
- `ResourceUseCasesTest`: search resources and update bookmark state.
- `SearchEverythingUseCaseTest`: group matching results across repositories.
- `AnalyticsUseCasesTest`: observe analytics and calculate streaks.
- `SettingsUseCasesTest`: observe and update theme, dynamic color, reminder, and first-launch settings.

These tests use fake repository implementations. They test domain behavior without starting Android framework components or a database.

## ViewModel Tests

No ViewModel tests currently exist. The ViewModels contain meaningful debounce, filtering, editor, error, and effect behavior, so adding tests with `kotlinx-coroutines-test` would provide strong value.

Recommended targets:

- `SearchViewModel` debounce and latest-query behavior.
- `RoadmapsViewModel` category and query filtering.
- `NotesViewModel` editor validation and effects.
- `TopicDetailViewModel` completion/bookmark effects.
- `SettingsViewModel` success/failure effects.

## Repository Tests

Use cases are tested through fake repositories, but concrete Room-backed repository implementations do not currently have dedicated tests. Repository tests should verify entity/domain mapping, combined Flow behavior, progress calculation, and bookmark joins.

## Room Tests

No Room instrumented tests currently exist. Priority DAO coverage should include:

- Note insert, update, search, and delete.
- Progress upsert and completion timestamps.
- Topic/resource bookmark insert and deletion.
- Goal upsert and observation.
- Database transaction behavior during demo seeding.
- Schema migration tests when database version 2 is introduced.

## Compose UI Tests

No Compose UI tests currently exist. Dependencies are configured, but test cases still need to be written.

Recommended flows:

- Roadmap list displays seeded tracks.
- Topic completion changes visible UI state.
- Notes can be created, edited, and deleted.
- Global search renders grouped results.
- Theme and reminder controls respond to settings state.
- Analytics cards render repository values.

## WorkManager And DataStore Tests

There are no WorkManager integration tests or DataStore instrumentation tests. Future tests should verify unique work scheduling/cancellation and preference persistence. The worker itself currently returns success without posting a notification.

## Running Verification

Build the debug APK:

```bash
./gradlew :app:assembleDebug
```

Run local unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run Android lint:

```bash
./gradlew :app:lintDebug
```

Build instrumented tests when they are added:

```bash
./gradlew :app:assembleDebugAndroidTest
```

Run connected tests with a device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Testing Priorities

1. Add coroutine test utilities and ViewModel tests.
2. Add Room DAO tests for critical writes and queries.
3. Add Compose UI tests for complete user workflows.
4. Add WorkManager and DataStore integration tests.
5. Add migration tests before changing the Room schema.

## Pull Request Checklist

- Add tests for new business rules.
- Keep test names behavior-focused.
- Prefer deterministic fake data and clocks where time affects results.
- Run build, unit tests, and lint locally.
- Include screenshots for UI changes until automated screenshot testing exists.
