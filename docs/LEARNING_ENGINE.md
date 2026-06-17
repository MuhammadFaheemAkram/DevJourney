# Learning Engine

The learning engine is the set of rules that turns roadmap content and user activity into progress, recommendations, analytics, and search results.

## Demo Catalog

`DemoLearningCatalog` provides deterministic content for:

- Android
- Backend
- AI Engineering
- System Design

The fake API adds a small delay to simulate network work, then returns DTOs. `DemoDataSeeder` maps DTOs to Room entities and writes them in a transaction when the app starts with an empty database.

## Progress Tracking

Users can mark topics complete from topic detail and roadmap detail. Completion writes a `ProgressEntity` and repositories recalculate roadmap and section progress from topic and progress flows.

Completion percentage is derived rather than stored:

```text
completed topics for roadmap / total topics for roadmap
```

## Notes

Notes are local-first and can optionally attach to a topic. Search checks note title and content. Topic detail observes notes for the current topic.

## Goals

Goals support daily, weekly, and monthly cadences. Progress is clamped between zero and the target count. Goal completion rate averages clamped progress across goals.

## Challenges

Challenges track status and optional solution notes. Completing a challenge normalizes blank notes to `null` and sets status to `COMPLETED`.

## Resources

Resources can be searched by title, description, and source. Bookmark state is joined from the shared bookmark table and exposed as a simple Boolean in `LearningResource`.

## Analytics

Analytics combines topics, progress, and goals:

- Streak days from consecutive completion dates.
- Completed topic count.
- Total topic count.
- Overall completion rate.
- Topics completed in the last seven days.
- Topics completed in the last thirty days.
- Goal completion rate.

## Search

Global search queries roadmaps, topics, notes, resources, and challenges. The use case returns grouped results so the UI can render sections without owning search business logic.

## Reminder Scheduling

The settings reminder toggle writes to DataStore and schedules or cancels unique periodic WorkManager work. The current worker is intentionally lightweight; notification permission handling can be added later without changing the settings UI contract.
