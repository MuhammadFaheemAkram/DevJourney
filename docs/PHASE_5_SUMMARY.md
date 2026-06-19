# Phase 5 Summary: Challenges, Resources, And Search

## Phase Goal

Expand the learning workflow beyond topics by adding coding challenge tracking, resource discovery/bookmarking, and one debounced search surface across the app.

## What Was Implemented

- Room-backed coding challenge list.
- Challenge difficulty and status filters.
- Challenge completion dialog with optional solution notes.
- Room-backed resource list.
- Resource type filters and text search.
- Resource bookmark toggles through the shared bookmark table.
- Global search across roadmaps, topics, notes, resources, and challenges.
- Grouped global search results.
- 300 ms query debounce with cancellation of stale work.
- Navigation from search results into roadmap/topic detail.
- Unit tests for challenge, resource, and global search use cases.

## Android Concepts Demonstrated

- Multiple independent filters in ViewModel state.
- Domain enums rendered as UI filter labels.
- Debounced search with Flow operators.
- `flatMapLatest` for latest-query ownership.
- One use case combining several repository streams.
- Shared bookmark persistence across target types.
- Dialog state and one-time effects.

## Data Flow

```text
Search query MutableStateFlow
   -> debounce(300)
   -> distinctUntilChanged
   -> flatMapLatest
   -> SearchEverythingUseCase
   -> roadmap/topic/note/resource/challenge repositories
   -> grouped SearchEverythingResults
   -> SearchUiState
   -> grouped Compose sections
```

```text
Bookmark or challenge action
   -> feature ViewModel
   -> use case
   -> repository
   -> BookmarkDao / ChallengeDao
   -> observed Flow updates UI
```

## Important Files

- `feature/challenges/ChallengesScreen.kt`: filters, cards, and completion dialog.
- `feature/challenges/ChallengesViewModel.kt`: filters, editor state, action, and effect.
- `domain/usecase/challenges/ChallengeUseCases.kt`: observe and complete operations.
- `feature/resources/ResourcesScreen.kt`: search, type chips, resource cards, and bookmarks.
- `feature/resources/ResourcesViewModel.kt`: debounced resource flow and bookmark effects.
- `domain/usecase/resources/ResourceUseCases.kt`: search and bookmark operations.
- `feature/search/SearchScreen.kt`: grouped global results.
- `feature/search/SearchViewModel.kt`: debounced latest-query UI state.
- `domain/usecase/search/SearchEverythingUseCase.kt`: cross-repository search aggregation.
- Phase 5 use-case test files under `app/src/test`.

## Important Classes

- `ChallengesViewModel`: combines challenge data, difficulty filter, status filter, and completion editor.
- `CompleteChallengeUseCase`: normalizes solution notes before completion.
- `ResourcesViewModel`: combines debounced search results with resource type filtering.
- `SearchViewModel`: ensures UI only displays results for the current query.
- `SearchEverythingUseCase`: returns grouped results across five domains.

## Interview Notes

### Why use `flatMapLatest` for search?

When a new query arrives, the old search stream is no longer relevant. `flatMapLatest` cancels collection of stale results and prevents an older query from overwriting newer UI state.

### Why group results in the use case?

The grouping is search-domain behavior. Keeping it outside Compose means UI only renders sections and does not coordinate five repositories.

### Why share one bookmark table?

The target id/type pair supports multiple bookmarkable domains with one persistence mechanism. The tradeoff is weaker foreign-key enforcement than dedicated join tables.

## Learning Checklist

- [x] Model challenge completion state.
- [x] Combine multiple filters.
- [x] Normalize optional solution notes.
- [x] Search resources reactively.
- [x] Persist resource bookmarks.
- [x] Debounce global search.
- [x] Aggregate multiple repositories.
- [ ] Add ViewModel debounce and Compose UI tests.

## Future Improvements

- Add challenge creation and in-progress actions.
- Add resource opening and invalid URL handling.
- Add result ranking, highlighting, and recent searches.
- Move heavy global search to indexed database queries as data grows.
- Add paging for large remote catalogs.
