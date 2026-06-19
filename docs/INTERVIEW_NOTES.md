# Interview Notes

These questions use DevJourney as a concrete example. Answers describe the implementation that exists today and identify how it could evolve.

## Architecture And MVVM

### Why use MVVM in a Compose app?

MVVM keeps long-lived screen state and business actions outside composables. In DevJourney, ViewModels combine repository data with search, filter, or editor state and expose immutable StateFlow. Composables render that state and send events back, which reduces logic in UI functions and improves lifecycle handling.

### Why expose immutable StateFlow?

Callers should observe state without being able to mutate it. StateFlow always has a current value, works naturally with `collectAsStateWithLifecycle`, and lets ViewModels control all state transitions. MutableStateFlow remains private.

### Why have use cases if repositories already expose operations?

Use cases provide intention-revealing APIs and a place for business rules such as trimming challenge solution notes, clamping goal progress, or combining global search repositories. They also make unit tests target user actions rather than storage details. Thin wrappers are acceptable when they keep feature APIs consistent.

### Why use repository interfaces?

They separate domain/feature code from Room, DataStore, WorkManager, and fake API implementations. Tests can substitute fake repositories, and a future real backend can be introduced without changing every ViewModel.

### Why separate DTO, entity, domain, and UI models?

Each model changes for different reasons. DTOs match transport data, entities match database schema, domain models express app behavior, and UI state includes presentation concerns such as loading flags and selected filters. Separating them prevents a backend or schema change from leaking through the whole app.

## Compose And State

### How does unidirectional data flow work here?

Repositories emit data, use cases expose operations, ViewModels derive UI state, Compose renders it, and UI events return to ViewModel functions. The ViewModel is the state owner for feature interactions.

### Why use `collectAsStateWithLifecycle`?

It collects Flow only while the composable lifecycle is active enough, reducing unnecessary work when the screen is stopped and integrating Flow safely with Compose state.

### How are one-time events handled?

Persistent screen data is StateFlow. Transient events use sealed effect interfaces over SharedFlow. Route composables collect effects in `LaunchedEffect` and show snackbars. This avoids storing a consumed snackbar message as durable screen state.

### How are loading, empty, and error states represented?

Feature UI state typically starts with `isLoading = true`, emits loaded lists/models, catches Flow failures into `errorMessage`, and computes an `isEmpty` state only after loading succeeds. Shared feedback composables render these cases consistently.

### How is debounced global search implemented?

`SearchViewModel` stores the query in MutableStateFlow, applies a 300 ms debounce and `distinctUntilChanged`, then uses `flatMapLatest` so old searches are cancelled when a newer query arrives. Results are grouped by `SearchEverythingUseCase`.

## Persistence And Offline Behavior

### Why use Room?

The app has relational, queryable data with multiple entity types, search queries, bookmarks, and observable lists. Room provides compile-time SQL validation, suspend writes, Flow queries, transactions, and schema export.

### Why use DataStore instead of Room for settings?

Theme, dynamic color, reminder state, selected roadmap id, and first-launch state are small preferences without relational queries. DataStore provides asynchronous, transactional preference updates and Flow observation with less schema overhead.

### What does offline-first mean in this project?

After the fake catalog is seeded, UI reads and writes go through local Room/DataStore sources. The app does not require a network backend. This is local-first behavior, not a production two-way sync engine.

### How does fake API seeding work?

`DevJourneyApp` launches `DemoDataSeeder`. The seeder checks whether Room contains roadmaps, calls the fake API only when empty, maps DTOs to entities, and inserts the complete catalog in a Room transaction.

### How would you replace the fake API with a real backend?

Add a real network client and DTOs behind the network/data layer, keep repository contracts stable, map responses into entities, and continue exposing Room as the UI source of truth. Then add refresh policies, errors, retries, authentication, conflict resolution, and WorkManager-based synchronization.

### What sync problems are currently unsolved?

There is no two-way sync, conflict policy, pagination, retry queue, remote deletion strategy, or versioning. Demo seeding only writes once when the database is empty.

## Dependency Injection And Background Work

### How does Hilt help this project?

Hilt constructs Room, DAOs, repositories, use cases, ViewModels, DataStore sources, and schedulers. Dependencies are explicit in constructors, implementations are bound centrally, and tests can work against interfaces.

### Why inject a coroutine dispatcher into the seeder?

It avoids hard-coding execution context and makes IO ownership explicit. In larger test setups, the dispatcher can be replaced for deterministic execution.

### Why use WorkManager for reminders?

WorkManager persists deferrable periodic work across process death and app restarts. The app enqueues unique daily work so toggling the setting does not create duplicate jobs.

### Does the app currently show notifications?

No. It schedules a periodic worker, but `LearningReminderWorker` returns success without posting a notification. Notification channels, runtime permission handling, and content are future work.

## Navigation

### How does navigation work?

One `NavHostController` is owned by `DevJourneyAppShell`. Bottom navigation and the drawer navigate to top-level string routes. Roadmap/topic detail helpers build routes with ids, and the top app bar shows back navigation for detail screens.

### How would navigation scale?

Introduce typed routes or serializable destinations, move feature navigation registration behind feature APIs, and split navigation graphs by module. Deep links and saved-state restoration should receive dedicated tests.

## Testing And Scaling

### What is tested today?

JUnit local tests cover calculation helpers and domain use cases with fake repositories. There are no ViewModel, Room instrumented, Compose UI, or WorkManager integration tests yet.

### What tests would you add first?

ViewModel coroutine tests for debounce/effects, Room DAO tests for critical writes, and Compose UI tests for roadmap progress, notes CRUD, search, and settings. Migration tests become mandatory before changing database version 1.

### How would you scale this project for a larger team?

Keep repository contracts and models in domain modules, extract database/network/design-system core modules, create feature Gradle modules, define navigation APIs between features, add a real sync engine, improve test coverage, and establish code ownership and CI quality gates.

### What is the biggest current product gap?

Dashboard values are static rather than aggregated from repositories. Visible reminder notifications and comprehensive instrumented tests are also incomplete. These are documented as future improvements rather than hidden.

### What tradeoff does the single-module structure make?

It keeps setup and navigation simple for a portfolio learning app, but offers less build isolation and ownership enforcement. The package boundaries are designed so modularization can happen when scale justifies it.
