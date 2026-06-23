# DevJourney

DevJourney is an offline-first Android learning hub for developers. It demonstrates a production-minded Kotlin codebase with Jetpack Compose, MVVM, clean architecture inspired boundaries, Hilt, Coroutines, Flow, StateFlow, Room, DataStore, Navigation Compose, and WorkManager.

The project is both a GitHub portfolio sample and a learning resource. Its implementation is complete through seven project phases, while the documentation calls out remaining gaps instead of presenting planned work as finished.

## Main Features

- Browse Android, Backend, AI Engineering, and System Design roadmaps.
- Search and filter roadmaps by title, description, and category.
- Explore roadmap sections and expand or collapse learning modules.
- Mark topics or whole sections complete and observe calculated progress.
- View topic objectives, difficulty, duration, notes, and learning resources.
- Bookmark topics and resources.
- Create, edit, delete, search, and attach notes to topics.
- Track daily, weekly, and monthly goals.
- Complete coding challenges and save solution notes.
- Search across roadmaps, topics, notes, resources, and challenges with debounce.
- View streak, completion, weekly/monthly, and goal analytics.
- Persist theme, dynamic color, reminder, selected roadmap, and first-launch settings.
- Schedule or cancel a daily WorkManager task from Settings.

The Dashboard currently demonstrates the visual shell with static sample values and links into the repository-backed roadmap flow. Making Dashboard aggregation live is listed under future improvements. The reminder worker is scheduled correctly but intentionally returns success without posting a system notification yet.

## Tech Stack

- Kotlin 2.2
- Jetpack Compose with Material 3
- Navigation Compose
- MVVM with immutable `StateFlow`
- Coroutines and Flow
- Hilt dependency injection
- Room database with KSP code generation
- DataStore Preferences
- WorkManager
- JUnit 4 and fake repositories for local unit tests
- AndroidX Compose test and Espresso dependencies configured for future instrumented tests

## Architecture Summary

DevJourney uses a single Android application module organized into clear layers:

```text
Compose UI
   -> ViewModel
   -> Use case
   -> Repository contract
   -> Repository implementation
   -> Room / DataStore / Fake API / WorkManager
```

Room is the local source of truth for learning data. DataStore owns lightweight user preferences. A deterministic fake API supplies demo catalog DTOs, which are mapped to Room entities during first-run seeding. Repositories map storage data into domain models, while ViewModels transform use-case flows into feature UI state.

See [Architecture](docs/ARCHITECTURE.md) for the complete data-flow explanation.

## Screens And Modules

| Area | Implementation |
| --- | --- |
| Dashboard | Static Compose overview shell with navigation into Roadmaps. |
| Roadmaps | Room-backed list, search, category filters, progress, and detail navigation. |
| Topic Detail | Objectives, resources, notes, completion, and topic bookmarking. |
| Notes | Room-backed CRUD, search, and topic attachment. |
| Goals | Room-backed cadence filtering and progress updates. |
| Challenges | Room-backed filters, completion state, and solution notes. |
| Resources | Room-backed search, filters, and bookmarks. |
| Search | Debounced global search across five content types. |
| Analytics | Derived Room-backed streak and completion metrics. |
| Settings | DataStore-backed theme and reminder preferences. |
| About | Static project and architecture summary. |

## Folder Structure

```text
co.bitfuse.devjourney/
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

## How To Run

Requirements:

- Android Studio with Android SDK 36 installed.
- JDK 17 for Gradle and CI compatibility.
- An emulator or device running Android 7.0 (API 24) or later.

Open the project in Android Studio and run the `app` configuration, or build from the terminal:

```bash
./gradlew :app:assembleDebug
```

## Fake API And Demo Data

`InMemoryFakeLearningApi` waits briefly to simulate network work and returns `DemoLearningCatalog`. The catalog contains roadmaps, topics, notes, goals, challenges, resources, progress records, and bookmarks.

`DemoDataSeeder` runs from `DevJourneyApp`. If Room has no roadmaps, it fetches the fake catalog, maps DTOs to entities, and inserts the complete data set in one Room transaction. Replacing the fake API with a real backend would primarily affect the network/data implementation, not feature UI contracts.

## Offline And Local Storage

- Room stores all relational learning content and user-generated learning data.
- DAOs expose important lists as `Flow`.
- Repository implementations combine entities, progress, and bookmark streams into domain models.
- DataStore persists theme, dynamic color, reminder state, selected roadmap id, and first-launch state.
- WorkManager owns the unique periodic reminder task.
- No backend connection is required after demo data seeding.

## Testing

Run local unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run Android lint:

```bash
./gradlew :app:lintDebug
```

Current tests cover calculation helpers and domain use cases with fake repositories. ViewModel, Room instrumented, Compose UI, and WorkManager integration tests are not implemented yet. See [Testing](docs/TESTING.md) for the exact test inventory and recommended additions.

## Learning Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Testing](docs/TESTING.md)
- [Learning Notes](docs/LEARNING_NOTES.md)
- [Interview Notes](docs/INTERVIEW_NOTES.md)
- [Data Model](docs/DATA_MODEL.md)
- [Learning Engine](docs/LEARNING_ENGINE.md)
- [Project Roadmap](docs/ROADMAP.md)
- [Screenshots](docs/SCREENSHOTS.md)
- [Phase 1 Summary](docs/PHASE_1_SUMMARY.md)
- [Phase 2 Summary](docs/PHASE_2_SUMMARY.md)
- [Phase 3 Summary](docs/PHASE_3_SUMMARY.md)
- [Phase 4 Summary](docs/PHASE_4_SUMMARY.md)
- [Phase 5 Summary](docs/PHASE_5_SUMMARY.md)
- [Phase 6 Summary](docs/PHASE_6_SUMMARY.md)
- [Phase 7 Summary](docs/PHASE_7_SUMMARY.md)

## Screenshots

Screenshot capture guidance and planned filenames live in [Screenshots](docs/SCREENSHOTS.md). Image slots are intentionally placeholders until emulator captures are added under `docs/images/`.

## Roadmap

The original seven phases are complete. Realistic next steps include repository-backed Dashboard aggregation, Room and Compose UI tests, visible notification delivery, accessibility review, screenshot assets, database migration tests, and optional modularization. See [Roadmap](docs/ROADMAP.md).

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md) before opening a contribution.

## License

DevJourney is available under the [MIT License](LICENSE).
