# Screenshots

This file tracks screenshot capture guidance for the project. Add images under `docs/images/` when publishing the repository.

## Recommended Screenshots

- `dashboard.png`: streak, progress, goals, and recent activity.
- `roadmaps.png`: roadmap list with filters and progress.
- `roadmap-detail.png`: expandable sections and completed topics.
- `topic-detail.png`: topic objectives, resources, notes, completion, and bookmark state.
- `notes.png`: note list and editor.
- `goals.png`: cadence filters and goal progress cards.
- `challenges.png`: difficulty/status filters and challenge cards.
- `resources.png`: resource search and bookmarks.
- `search.png`: grouped global search results.
- `analytics.png`: streak, metric cards, and completion chart.
- `settings.png`: theme, dynamic color, reminders, and DataStore-backed preferences.

## Capture Checklist

- Use a clean emulator state with seeded demo data.
- Prefer light and dark theme examples for Settings and Analytics.
- Avoid personal data in notes or solution notes.
- Keep status bar and navigation bar visible unless cropping for a store-style image.
- Re-run `./gradlew :app:assembleDebug` before taking final screenshots.

## README Usage

After adding images, update `README.md` with a small gallery:

```markdown
![Dashboard](docs/images/dashboard.png)
![Roadmaps](docs/images/roadmaps.png)
![Analytics](docs/images/analytics.png)
```
