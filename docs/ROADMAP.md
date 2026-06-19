# Roadmap

DevJourney is complete through its original seven-phase build. The items below are realistic next improvements based on gaps in the current codebase.

## Completed Project Phases

1. Foundation: Compose, Material 3, Hilt, navigation shell, design system, and feature routes.
2. Data layer: domain models, Room, DAOs, fake API, mappers, repositories, and demo seeding.
3. Roadmaps and topics: list/detail flows, filtering, completion, section progress, and bookmarks.
4. Notes and goals: notes CRUD/search and cadence-based goal tracking.
5. Challenges, resources, and search: filters, completion, bookmarks, and debounced global search.
6. Analytics and settings: derived analytics, DataStore preferences, and WorkManager scheduling.
7. Open-source polish: README, docs, contribution files, templates, license, and CI.

See the individual `PHASE_X_SUMMARY.md` files for implementation and learning details.

## Priority 1: Close Existing Functional Gaps

- Replace static Dashboard values with a repository-backed Dashboard ViewModel and aggregation use case.
- Post a real reminder notification from `LearningReminderWorker`.
- Add Android 13+ notification permission handling and notification channel creation.
- Connect selected roadmap persistence to an explicit user-facing learning-plan workflow.

## Priority 2: Testing

- Add `kotlinx-coroutines-test` and ViewModel tests.
- Add Room DAO tests for notes, progress, bookmarks, goals, challenges, and resources.
- Add Compose UI tests for roadmap/topic progress, notes CRUD, search, analytics, and settings.
- Add DataStore and WorkManager integration tests.
- Add database migration tests before increasing the Room schema version.

## Priority 3: Offline Sync

- Replace the fake API with a real HTTP client behind the existing data boundaries.
- Add refresh timestamps and explicit sync state.
- Add retry/backoff and network constraints.
- Define conflict resolution for progress, notes, goals, and bookmarks.
- Add an upload queue for local writes.
- Add pagination if catalog/resource size grows.

## Priority 4: UI And Accessibility

- Capture and publish screenshots under `docs/images`.
- Audit semantics, touch targets, contrast, and screen-reader labels.
- Add adaptive layouts for tablets and foldables.
- Add richer Markdown rendering for notes.
- Improve empty/error recovery actions.
- Add visual regression or screenshot tests for design-system components.

## Priority 5: Project Scale

- Modularize into core, domain, data, and feature Gradle modules when build time warrants it.
- Introduce typed navigation and feature navigation APIs.
- Add dependency update automation and stricter CI checks.
- Add coverage reporting and static analysis beyond Android lint.
- Document release signing and distribution once the app has a release target.

## Optional Product Ideas

- Import/export notes and progress.
- Multiple learning plans.
- Achievement badges and streak recovery.
- Recommendations based on difficulty, progress, goals, and bookmarks.
- Optional authenticated cloud sync.

These ideas are not implemented and should remain behind clear product requirements before architecture is expanded.
