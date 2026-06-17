# DevJourney

DevJourney is an offline-first Android learning management app for developers. It combines roadmap browsing, topic progress, notes, goals, coding challenges, resources, analytics, search, and persistent settings in a modern Kotlin and Jetpack Compose codebase.

The project is designed as a GitHub portfolio sample for production-minded Android engineering: MVVM, clean architecture inspired layers, Hilt, Coroutines, Flow, StateFlow, Room, DataStore, WorkManager, Navigation Compose, Material 3, fake API synchronization, tests, and open-source project hygiene.

## Screens And Features

- Dashboard with streak, progress, active roadmap, goals, notes, and challenge summaries.
- Roadmaps list with category filtering, search, completion progress, and roadmap details.
- Topic detail with objectives, difficulty, duration, resources, notes, completion, and bookmark state.
- Notes CRUD with topic attachment and search.
- Daily, weekly, and monthly learning goals with progress controls.
- Coding challenges with difficulty/status filtering and saved solution notes.
- Resources with search, type filters, and bookmarks.
- Global search across roadmaps, topics, notes, resources, and challenges.
- Analytics with learning streak, completion rate, weekly/monthly progress, goal rate, and a custom Compose chart.
- Settings with persisted theme, dynamic color, daily reminder, selected roadmap, and first-launch state.
- Full offline behavior using deterministic fake API data and local persistence.

## Tech Stack

- Kotlin
- Jetpack Compose and Material 3
- Navigation Compose
- MVVM with immutable `StateFlow` UI state
- Coroutines and Flow
- Hilt dependency injection
- Room for local app data
- DataStore Preferences for user settings
- WorkManager for reminder scheduling
- JUnit and fake repositories for unit tests

## Architecture

DevJourney uses clean architecture inspired boundaries without unnecessary ceremony:

```text
com.example.devjourney/
  DevJourneyApp.kt
  MainActivity.kt
  MainViewModel.kt
  core/
    common/
    database/
    datastore/
    designsystem/
    navigation/
    network/
    notification/
  data/
    analytics/
    challenges/
    goals/
    mapper/
    notes/
    progress/
    repository/
    resources/
    roadmap/
    settings/
    topic/
  domain/
    model/
    repository/
    usecase/
  di/
  feature/
    analytics/
    challenges/
    dashboard/
    goals/
    notes/
    resources/
    roadmap/
    search/
    settings/
    topicdetail/
```

Key rules followed in the app:

- Feature screens are mostly stateless and delegate behavior to ViewModels.
- ViewModels expose immutable `StateFlow` and one-time effects through shared flows.
- UI collects state with `collectAsStateWithLifecycle`.
- Repositories hide Room, DataStore, WorkManager, and fake API details from the UI.
- DTO, entity, domain, and UI state concerns stay separated through mappers and use cases.
- Loading, empty, success, and error states are modeled explicitly in UI state.

More detail is available in [Architecture](docs/ARCHITECTURE.md), [Data Model](docs/DATA_MODEL.md), and [Learning Engine](docs/LEARNING_ENGINE.md).

## Fake API And Offline-First Flow

No backend is required. `FakeLearningApi` returns deterministic demo content for Android, Backend, AI Engineering, and System Design learning tracks. On app startup, `DemoDataSeeder` syncs that catalog into Room when the local database is empty.

After seeding:

- Room is the source of truth for roadmaps, topics, notes, goals, challenges, resources, progress, and bookmarks.
- Repositories expose `Flow` streams from DAOs.
- User settings are persisted with DataStore and never accessed directly from composables.
- Reminder state schedules or cancels a unique WorkManager job.
- The app keeps working offline because all user-facing reads and writes are local.

## Navigation

Bottom navigation contains:

- Dashboard
- Roadmaps
- Challenges
- Analytics
- Settings

The drawer adds:

- Notes
- Goals
- Resources
- Search
- About

Roadmap and topic details use typed route arguments and keep top-level selection stable while the back stack moves into detail screens.

## How To Run

Open the project in Android Studio, choose the `app` configuration, and run it on an emulator or physical device.

From a terminal:

```bash
./gradlew :app:assembleDebug
```

## Testing

Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run lint:

```bash
./gradlew :app:lintDebug
```

Current coverage includes progress calculations, learning streak logic, goal progress, notes use cases, goals use cases, challenge completion, resource search/bookmarking, global search grouping, analytics use cases, and settings use cases.

The testing approach is documented in [Testing](docs/TESTING.md).

## Roadmap

Completed phases:

- Phase 1: Foundation, Hilt, theme, app shell, drawer, bottom navigation, placeholder screens.
- Phase 2: Models, Room, DAOs, fake API, repositories, Hilt modules, demo data.
- Phase 3: Roadmaps, roadmap details, topic details, progress tracking, bookmarking.
- Phase 4: Notes, goals, progress calculations, and tests.
- Phase 5: Challenges, resources, global search, bookmarks, and tests.
- Phase 6: Analytics, DataStore settings, WorkManager reminders, and tests.
- Phase 7: Documentation, CI workflow, templates, README finalization, and open-source polish.

Future work is tracked in [Roadmap](docs/ROADMAP.md).

## Screenshots

Screenshot slots and capture guidance live in [Screenshots](docs/SCREENSHOTS.md). Add images under `docs/images/` as the UI evolves.

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md) before opening issues or pull requests.

## License

DevJourney is available under the MIT License. See [LICENSE](LICENSE).
