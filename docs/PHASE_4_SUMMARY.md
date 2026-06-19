# Phase 4 Summary: Notes And Goals

## Phase Goal

Add user-generated learning content and measurable commitments: local notes with CRUD/search and daily, weekly, or monthly goal progress.

## What Was Implemented

- Observe notes from Room in updated order.
- Create, edit, and delete notes.
- Search note title and content.
- Attach an optional topic to a note.
- Note editor validation and snackbar effects.
- Observe goals from Room.
- Filter goals by daily, weekly, or monthly cadence.
- Increment, decrement, and complete goal progress.
- Clamp progress to valid bounds.
- Derive completed-goal count and average completion rate.
- Unit tests for note use cases, goal use cases, and calculations.

## Android Concepts Demonstrated

- CRUD with Room-backed repositories.
- Search queries and reactive results.
- Dialog/editor state in a ViewModel.
- Domain validation and normalization.
- Derived UI summaries from domain state.
- Fake repositories for use-case tests.
- SharedFlow effects for transient feedback.

## Data Flow

```text
NoteDao / GoalDao Flow
   -> NotesRepositoryImpl / GoalsRepositoryImpl
   -> Observe use case
   -> NotesViewModel / GoalsViewModel
   -> StateFlow
   -> Compose screen

Create or update action
   -> ViewModel validates input
   -> use case
   -> repository
   -> DAO write
   -> observed Flow emits new list
```

## Important Files

- `feature/notes/NotesScreen.kt`: list, search, editor, and delete confirmation UI.
- `feature/notes/NotesViewModel.kt`: editor/search state, validation, CRUD actions, and effects.
- `data/notes/NotesRepositoryImpl.kt`: entity/domain mapping and DAO operations.
- `domain/usecase/notes/NoteUseCases.kt`: CRUD/search use cases.
- `feature/goals/GoalsScreen.kt`: cadence filters, summaries, and progress controls.
- `feature/goals/GoalsViewModel.kt`: goal observation, filtering, updates, and summary state.
- `data/goals/GoalsRepositoryImpl.kt`: goal persistence and clamping support.
- `domain/usecase/goals/GoalUseCases.kt`: observe/update actions and calculations.
- `NoteUseCasesTest.kt`, `GoalUseCasesTest.kt`, `ProgressCalculationsTest.kt`: Phase 4 unit coverage.

## Important Classes

- `NotesViewModel`: owns the current query and note editor state.
- `NotesRepositoryImpl`: maps notes and performs CRUD through `NoteDao`.
- `GoalsViewModel`: combines goals with cadence selection and summary calculations.
- `GoalProgressCalculator`: calculates per-goal and aggregate progress.
- `LearningGoal`: domain model for target/current count and cadence.

## Interview Notes

### Where should note validation happen?

The ViewModel can handle presentation validation such as required editor fields, while use cases should own reusable domain normalization. Persistence should reject invalid data only as a final safeguard.

### Why derive goal summaries in ViewModel state?

The summary is presentation-oriented and changes whenever goals change. Deriving it from observed goals avoids storing another source of truth.

### Why use fake repositories in tests?

They make resulting state observable without Android framework or Room setup and test the use case contract rather than SQL details.

## Learning Checklist

- [x] Implement Room-backed CRUD.
- [x] Search notes reactively.
- [x] Keep editor state outside composables.
- [x] Attach notes to optional topics.
- [x] Model goal cadence.
- [x] Clamp goal progress.
- [x] Add domain/use-case unit tests.
- [ ] Add Room and Compose integration tests.

## Future Improvements

- Add Markdown rendering and preview for notes.
- Add note export/import.
- Add custom goal creation and deletion workflows.
- Add due dates and reminder integration for goals.
- Add DAO and Compose UI tests for CRUD behavior.
