# Phase 3 Summary: Roadmaps And Topics

## Phase Goal

Turn the learning catalog into a real user workflow: browse and filter roadmaps, inspect a structured path, open topic details, track completion, and bookmark topics.

## What Was Implemented

- Room-backed roadmap list.
- Roadmap search and category filtering.
- Completion percentage per roadmap.
- Roadmap detail navigation by id.
- Expandable roadmap sections with nested topics.
- Topic and section completion actions.
- Topic detail with description, objectives, difficulty, duration, resources, and notes.
- Topic bookmarking.
- Hilt ViewModels with immutable StateFlow.
- Snackbar effects for completion and bookmark actions.

## Android Concepts Demonstrated

- Combining multiple Flow streams for derived models.
- ViewModel UI-state transformation.
- Nested and expandable Compose UI.
- Navigation arguments for detail screens.
- Use-case-driven write actions.
- SharedFlow for one-time effects.
- Calculated progress rather than duplicated stored percentages.

## Data Flow

```text
Roadmap/Topic DAO flows + Progress flow + Bookmark flow
   -> RoadmapRepositoryImpl / TopicRepositoryImpl
   -> domain models with completion and bookmark state
   -> observe/get use case
   -> RoadmapsViewModel / RoadmapDetailViewModel / TopicDetailViewModel
   -> StateFlow UI state
   -> Compose list or detail screen

User marks complete
   -> ViewModel
   -> MarkTopicCompletedUseCase
   -> repository write
   -> Room invalidates Flow
   -> updated progress recomposes UI
```

## Important Files

- `feature/roadmap/RoadmapsScreen.kt`: list, search, filters, and roadmap cards.
- `feature/roadmap/RoadmapsViewModel.kt`: query/category state and roadmap observation.
- `feature/roadmap/RoadmapDetailScreen.kt`: expandable sections and topic rows.
- `feature/roadmap/RoadmapDetailViewModel.kt`: detail state and section completion.
- `feature/topicdetail/TopicDetailScreen.kt`: complete topic learning view.
- `feature/topicdetail/TopicDetailViewModel.kt`: completion/bookmark actions and effects.
- `domain/usecase/roadmap/RoadmapUseCases.kt`: roadmap observation/refresh/detail operations.
- `domain/usecase/topic/TopicUseCases.kt`: topic observation and writes.
- `data/roadmap/RoadmapRepositoryImpl.kt`: progress-enriched roadmap models.
- `data/topic/TopicRepositoryImpl.kt`: topic, progress, and bookmark coordination.

## Important Classes

- `RoadmapsViewModel`: combines roadmaps, search query, and selected category.
- `RoadmapDetailViewModel`: observes one roadmap tree and tracks expanded sections.
- `TopicDetailViewModel`: synchronizes topic, resource, note, completion, and bookmark state.
- `RoadmapRepositoryImpl`: calculates roadmap/section completion from local data.
- `TopicRepositoryImpl`: owns topic completion and bookmark persistence.

## Interview Notes

### Why calculate completion instead of storing it on RoadmapEntity?

Completion is derived from topic progress. Calculating it avoids stale duplicated state and lets Room Flow updates propagate automatically.

### How does a section-complete action work?

The ViewModel identifies incomplete topics, calls the topic completion use case for them, and the observed Room data emits updated section progress.

### Why use navigation callbacks from screens?

It keeps feature UI independent from a specific NavController and centralizes route construction in navigation helpers.

## Learning Checklist

- [x] Combine catalog and progress flows.
- [x] Build search and category filters in ViewModel state.
- [x] Render expandable nested learning content.
- [x] Pass ids through Navigation Compose routes.
- [x] Persist topic completion.
- [x] Persist topic bookmarks.
- [x] Emit one-time action feedback.
- [ ] Add ViewModel and Compose UI tests.

## Future Improvements

- Add sort and difficulty filters.
- Add explicit retry UI for refresh failures.
- Add ViewModel tests for filtering and section completion.
- Add Compose tests for detail navigation and progress updates.
- Add richer roadmap visualization and accessibility semantics.
