# Roadmap

DevJourney is complete through its initial seven-phase portfolio build. Future work can deepen production readiness and polish.

## Completed

### Phase 1: Foundation

- Compose app shell.
- Hilt bootstrap.
- Material 3 theme and design system.
- Drawer and bottom navigation.
- Placeholder feature routes.

### Phase 2: Data Layer

- Domain models and repository contracts.
- Room entities, DAOs, database, and schema export.
- Fake API DTOs and deterministic demo catalog.
- Mapper layer and repository implementations.
- Startup demo data seeding.

### Phase 3: Roadmaps And Topics

- Roadmap list, filters, and search.
- Roadmap detail with expandable sections.
- Topic detail with objectives, resources, notes, completion, and bookmarking.
- Progress tracking through use cases.

### Phase 4: Notes And Goals

- Notes CRUD and search.
- Topic attachment for notes.
- Daily, weekly, and monthly goal progress.
- Goal summary calculations and tests.

### Phase 5: Challenges, Resources, Search

- Coding challenge filters and completion flow.
- Resource search, type filters, and bookmarks.
- Global search across learning entities.
- Phase-specific use case tests.

### Phase 6: Analytics And Settings

- Analytics dashboard with streaks, completion rate, weekly/monthly progress, and goal rate.
- DataStore settings for theme, dynamic color, reminders, selected roadmap, and first launch.
- WorkManager reminder scheduling.
- Analytics and settings tests.

### Phase 7: Open-Source Polish

- README finalization.
- Documentation set.
- Contribution, conduct, security, changelog, and license files.
- GitHub issue templates, pull request template, and CI workflow.

## Near-Term Improvements

- Make the dashboard fully repository-backed.
- Add more Room DAO and migration tests.
- Add Compose UI tests for key workflows.
- Add screenshot assets and visual QA guidance.
- Add notification permission handling for Android 13+ if real notifications are introduced.
- Add import/export for notes and learning progress.

## Longer-Term Ideas

- Optional cloud sync behind repository interfaces.
- Multi-plan learning schedules.
- Rich Markdown rendering for notes.
- Achievement badges and more nuanced streak recovery.
- Learning recommendations based on progress, difficulty, and bookmarked resources.
- Modularization into `core`, `data`, `domain`, and feature Gradle modules.
