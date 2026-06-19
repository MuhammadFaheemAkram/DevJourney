# Learning Notes

This guide explains the Android concepts demonstrated by DevJourney. It is written for developers who can read Kotlin and want to understand how the pieces cooperate in a real Compose app.

## MVVM

Model-View-ViewModel separates rendering from state and behavior:

- Compose screens are the View.
- Feature UI state and domain models represent the data shown by the View.
- ViewModels observe use cases, derive UI state, and handle user intent.

This makes screens easier to preview and test because composables mostly receive values and callbacks. It also lets state survive recomposition and configuration changes through ViewModel ownership.

## Jetpack Compose

Compose builds UI from functions instead of XML layouts. DevJourney uses:

- `Scaffold`, top app bar, navigation bar, and modal drawer for the app shell.
- `LazyColumn` and `LazyRow` for lists and filter chips.
- Material 3 cards, switches, dialogs, buttons, and progress indicators.
- Reusable design-system composables for loading, empty, error, progress, resource, topic, and analytics UI.

The important mental model is that UI is a function of state. When a collected Flow emits a new value, Compose recomposes the affected UI.

## StateFlow

ViewModels expose immutable `StateFlow<UiState>`. A StateFlow always has a current value, which makes it suitable for screen state such as loading, content, selected filters, editor state, and errors.

Internally, ViewModels may own `MutableStateFlow` for interaction state. They do not expose that mutable type publicly. Many features combine repository data with local flows and convert the result to StateFlow using `stateIn`.

## Coroutines And Flow

Coroutines handle asynchronous work without callbacks. DevJourney launches writes in `viewModelScope`, uses an injected IO dispatcher for demo seeding, and implements the reminder worker as a `CoroutineWorker`.

Flow represents streams of values. Room DAO queries emit Flow, repositories transform and combine those streams, and ViewModels build UI state from them.

Useful operators demonstrated here include:

- `combine` for multiple sources.
- `map` for model transformation.
- `catch` for error-state conversion.
- `debounce` for search input.
- `distinctUntilChanged` to avoid duplicate searches.
- `flatMapLatest` to cancel stale search work.
- `stateIn` to expose lifecycle-friendly state.

## Repository Pattern

A repository is a boundary around data access. Feature code asks `TopicRepository` to observe a topic or update completion; it does not know whether the result came from Room, a fake API, or a future backend.

Repositories in DevJourney:

- Read DAO flows.
- Map entities into domain models.
- Combine topic/progress/bookmark data.
- Write user changes locally.
- Hide DataStore and WorkManager details behind `SettingsRepository`.

## Use Cases

Use cases give ViewModels focused, intention-revealing operations such as `MarkTopicCompletedUseCase`, `CreateNoteUseCase`, or `SearchEverythingUseCase`.

They are useful when they:

- Express a business action clearly.
- Normalize or validate input.
- Combine repositories.
- Make behavior easy to test with fakes.

Not every repository call needs a complicated use case. DevJourney keeps most wrappers small because the current business rules are small.

## Hilt

Hilt creates and connects dependencies. `@HiltAndroidApp` initializes the application graph, `@AndroidEntryPoint` makes dependencies available to `MainActivity`, and `@HiltViewModel` supports constructor-injected ViewModels.

Modules provide Room, DAOs, coroutine dispatchers, and fake network bindings. `@Binds` connects repository interfaces to implementations. Constructor injection keeps dependencies visible and avoids service-locator calls inside features.

## Room

Room provides a typed SQLite layer. DevJourney defines entities for roadmaps, topics, progress, notes, goals, challenges, resources, and bookmarks.

DAOs use suspend functions for writes and Flow for observable reads. Room invalidates affected queries after a write, so repository and UI streams update without manual refresh logic.

The schema is version 1 and exported. Migration tests should be added before future schema changes.

## DataStore

Preferences DataStore stores small user settings that do not need relational queries:

- Theme mode.
- Dynamic color.
- Reminder toggle.
- Selected roadmap id.
- First-launch flag.

`UserPreferencesDataSource` maps preference keys into `UserSettings`. The settings repository exposes the result as Flow, and `MainViewModel` lets the app theme react to updates.

## Navigation Compose

The app shell owns one `NavHostController`. String constants define top-level and detail routes. The bottom bar and drawer share the same controller, while helper functions implement single-top navigation and route argument construction.

Roadmap and topic details receive ids through navigation arguments. Navigation stays outside feature screen business logic: screens expose callbacks, and the app shell decides where those callbacks navigate.

## One-Time Effects

State should describe what remains true. A snackbar should happen once, so it is modeled separately as an effect.

DevJourney uses sealed effect interfaces and `SharedFlow`. ViewModels emit an effect after actions such as saving a note or bookmarking a resource. Route composables collect the effect inside `LaunchedEffect` and show the snackbar.

## WorkManager

WorkManager is appropriate for deferrable, persistent background work. The settings repository asks `LearningReminderScheduler` to enqueue or cancel unique daily periodic work.

The worker currently performs no visible notification work and returns success. This demonstrates scheduling boundaries, but notification channels, Android 13+ permission handling, and actual notification content remain future improvements.

## Offline-First Design

The app is local-first after seeding:

1. Fake API returns deterministic DTOs.
2. Seeder maps DTOs into Room entities.
3. Room becomes the source of truth.
4. Repositories expose local Flow streams.
5. User actions write locally and update observed streams.

A production sync engine would add remote/local conflict rules, retries, network constraints, timestamps or versions, and an upload queue. Those pieces are not present in this demo.

## Testing Lessons

Use-case tests demonstrate how repository interfaces make business behavior testable without Android framework setup. Fake repositories backed by MutableStateFlow are often clearer than mocks when the test cares about resulting state.

The project does not yet demonstrate ViewModel coroutine tests, Room instrumented tests, or Compose UI tests. Those are the highest-value next learning exercises.
