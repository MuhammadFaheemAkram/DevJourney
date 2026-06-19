# Phase 6 Summary: Analytics And Settings

## Phase Goal

Turn stored learning activity into useful metrics and add persistent app preferences with a background reminder scheduling boundary.

## What Was Implemented

- Room-backed analytics repository.
- Learning streak from consecutive completion dates.
- Completed/total topics and completion percentage.
- Weekly and monthly completion totals.
- Average goal completion rate.
- Analytics ViewModel, metric cards, progress card, streak banner, and Compose chart.
- Preferences DataStore for theme, dynamic color, reminder, selected roadmap, and first launch.
- App-wide theme observation through `MainViewModel`.
- Settings ViewModel with sealed effects.
- Unique daily WorkManager scheduling and cancellation.
- Analytics and settings use-case tests.

The worker does not currently post a visible notification; it returns success. This phase implements scheduling infrastructure, not full notification delivery.

## Android Concepts Demonstrated

- Aggregating multiple DAO flows.
- Pure analytics calculations.
- App-wide state observed above the navigation shell.
- Preferences DataStore with typed domain mapping.
- Dynamic Material color and theme selection.
- WorkManager unique periodic work.
- Repository coordination of preference persistence and platform scheduling.

## Data Flow

```text
TopicDao + ProgressDao + GoalDao
   -> AnalyticsRepositoryImpl.combine(...)
   -> LearningAnalytics
   -> ObserveAnalyticsUseCase
   -> AnalyticsViewModel StateFlow
   -> AnalyticsScreen
```

```text
Settings user action
   -> SettingsViewModel
   -> update use case
   -> DataStoreSettingsRepository
   -> UserPreferencesDataSource
   -> DataStore Flow
   -> SettingsViewModel and MainViewModel
   -> Settings UI and app theme

Reminder toggle additionally
   -> LearningReminderScheduler
   -> WorkManager enqueue/cancel unique work
```

## Important Files

- `data/analytics/AnalyticsRepositoryImpl.kt`: combines topics, progress, and goals.
- `domain/usecase/analytics/AnalyticsUseCases.kt`: analytics observation and streak calculation.
- `feature/analytics/AnalyticsViewModel.kt`: analytics UI state.
- `feature/analytics/AnalyticsScreen.kt`: metric and chart UI.
- `core/datastore/UserPreferencesDataSource.kt`: preference keys and UserSettings mapping.
- `data/settings/DataStoreSettingsRepository.kt`: settings persistence and reminder coordination.
- `domain/usecase/settings/SettingsUseCases.kt`: settings operations.
- `feature/settings/SettingsViewModel.kt`: settings state/actions/effects.
- `feature/settings/SettingsScreen.kt`: settings controls and About screen.
- `MainViewModel.kt`, `MainActivity.kt`: app-wide theme application.
- `core/notification/LearningReminderScheduler.kt`: unique periodic work.
- `core/notification/LearningReminderWorker.kt`: placeholder successful worker.

## Important Classes

- `AnalyticsRepositoryImpl`: derives analytics whenever underlying Room flows change.
- `LearningAnalytics`: domain metrics consumed by the screen.
- `UserPreferencesDataSource`: converts raw preferences to typed settings.
- `DataStoreSettingsRepository`: hides DataStore and WorkManager details.
- `MainViewModel`: exposes settings above feature navigation.
- `LearningReminderScheduler`: prevents duplicate periodic reminder work.

## Interview Notes

### Why is analytics derived instead of stored?

The current metrics can be calculated from progress, topics, and goals. Derivation avoids stale aggregate tables and updates naturally with Flow. At much larger scale, precomputed aggregates could be justified.

### Why observe theme settings in MainActivity?

Theme affects the entire composition. Observing it above `DevJourneyAppShell` lets a preference update recompose all screens consistently.

### Why schedule unique WorkManager work?

Repeated enabling should replace the existing daily job rather than creating duplicate reminders.

## Learning Checklist

- [x] Aggregate DAO flows into analytics.
- [x] Calculate streak and completion metrics.
- [x] Persist preferences with DataStore.
- [x] Apply theme state app-wide.
- [x] Schedule unique periodic work.
- [x] Test analytics and settings use cases.
- [ ] Post and test a visible notification.
- [ ] Add DataStore and WorkManager integration tests.

## Future Improvements

- Implement notification channels, permission flow, and visible reminders.
- Make reminder time configurable rather than only enabled/disabled.
- Inject a clock for deterministic analytics tests.
- Define timezone-aware streak semantics.
- Connect selected roadmap to a complete learning-plan workflow.
- Add DataStore corruption/recovery tests.
