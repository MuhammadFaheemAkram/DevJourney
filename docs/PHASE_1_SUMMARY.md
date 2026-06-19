# Phase 1 Summary: Foundation

## Phase Goal

Establish a runnable Compose application with dependency injection, Material 3 styling, reusable UI primitives, and a navigation shell that later features could fill without restructuring the app.

## What Was Implemented

- `DevJourneyApp` with Hilt application setup.
- `MainActivity` with edge-to-edge Compose content.
- Material 3 light/dark theme and typography.
- Reusable cards, badges, search field, chip group, progress UI, streak banner, and feedback states.
- App scaffold with top app bar, modal navigation drawer, and bottom navigation.
- Routes for Dashboard, Roadmaps, Notes, Goals, Challenges, Resources, Search, Analytics, Settings, About, and detail screens.
- Static Dashboard overview and initial feature placeholders.

## Android Concepts Demonstrated

- Compose functions and reusable UI components.
- Material 3 theming.
- Navigation Compose with one NavHostController.
- Scaffold ownership and adaptive system insets.
- Hilt application/activity setup.
- Stateless screen callbacks for navigation.

## Data Flow

```text
User taps navigation item
   -> DevJourneyAppShell callback
   -> NavHostController.navigate(...)
   -> DevJourneyNavGraph selects route
   -> Feature route composable renders
```

Phase 1 did not yet have repository-backed feature data.

## Important Files

- `DevJourneyApp.kt`: Hilt-enabled Application entry point.
- `MainActivity.kt`: Compose host activity.
- `core/designsystem/theme/Theme.kt`: Material 3 theme selection.
- `core/designsystem/component/`: reusable visual building blocks.
- `core/navigation/DevJourneyAppShell.kt`: scaffold, drawer, bottom bar, and top bar.
- `core/navigation/DevJourneyDestination.kt`: route constants and destination metadata.
- `core/navigation/DevJourneyNavGraph.kt`: route-to-screen registration.
- `feature/dashboard/DashboardScreen.kt`: static first-screen shell.

## Important Classes

- `DevJourneyApp`: creates the application-level Hilt graph.
- `MainActivity`: owns the root Compose composition.
- `DevJourneyAppShell`: owns shared navigation chrome.
- `DevJourneyDestination`: describes route title and icon.
- `DevJourneyTheme`: selects static or dynamic light/dark color schemes.

## Interview Notes

### Why keep navigation in the app shell?

It prevents feature composables from depending directly on NavController and makes screens easier to preview or test with callbacks.

### Why create a design system early?

Shared cards, inputs, and feedback states improve consistency and reduce repeated UI code as features are added.

### Why use Hilt before repositories exist?

Starting with the dependency graph avoids later changes to application/activity setup and establishes constructor injection as the project convention.

## Learning Checklist

- [x] Understand `@HiltAndroidApp` and `@AndroidEntryPoint`.
- [x] Build a Compose app shell with `Scaffold`.
- [x] Configure Material 3 light and dark themes.
- [x] Create reusable stateless composables.
- [x] Register routes with Navigation Compose.
- [x] Coordinate drawer and bottom navigation.
- [ ] Add automated Compose navigation tests.

## Future Improvements

- Add adaptive navigation for large screens.
- Replace static Dashboard values with live aggregation.
- Add Compose UI tests for drawer, bottom bar, and routes.
- Introduce typed routes if navigation grows.
