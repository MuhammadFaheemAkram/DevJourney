# Contributing To DevJourney

Thanks for helping improve DevJourney. This project is meant to demonstrate maintainable Android engineering, so contributions should keep the architecture readable, testable, and friendly to future contributors.

## Getting Started

1. Fork the repository and create a feature branch.
2. Open the project in Android Studio.
3. Build the debug app:

```bash
./gradlew :app:assembleDebug
```

4. Run tests before submitting:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

## Development Guidelines

- Keep business logic outside composables.
- Expose immutable `StateFlow` from ViewModels.
- Use repositories and use cases instead of reaching into data sources from UI code.
- Use Room, DataStore, and WorkManager through data-layer abstractions.
- Add or update tests when behavior changes.
- Keep UI states explicit: loading, empty, success, and error.
- Prefer existing design system components before adding new UI primitives.
- Keep changes scoped. Avoid unrelated refactors in feature pull requests.

## Pull Requests

Good pull requests include:

- A clear summary of the user-facing or architectural change.
- Screenshots or screen recordings for UI changes.
- Test results from local verification.
- Notes about tradeoffs, follow-up work, or intentionally deferred scope.

## Commit Style

Use short, imperative commit messages:

- `Add resource bookmark tests`
- `Persist settings with DataStore`
- `Document offline-first architecture`

## Issue Triage

When opening an issue, include reproduction steps, expected behavior, actual behavior, and environment details when relevant. For feature requests, describe the learning workflow the feature should improve.
