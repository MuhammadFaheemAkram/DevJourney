# Phase 2 Summary: Data Layer

## Phase Goal

Create the offline data foundation: models, Room storage, observable DAOs, fake API content, mapping, repositories, dependency injection, and first-run demo data seeding.

## What Was Implemented

- Domain models and repository contracts for every learning area.
- Room entities for roadmaps, topics, progress, notes, goals, challenges, resources, and bookmarks.
- DAOs with Flow queries and suspend writes.
- `DevJourneyDatabase` version 1 with schema export.
- Fake API DTOs and deterministic demo content.
- DTO-to-entity and entity-to-domain mappers.
- Room-backed repository implementations.
- Hilt modules for database, DAOs, repositories, fake network, dispatchers, and application scope.
- Transactional startup seeding when the database is empty.

## Android Concepts Demonstrated

- Room entities, DAOs, database, transactions, and type converters.
- Flow as an observable database API.
- Repository pattern and dependency inversion.
- DTO/entity/domain model separation.
- Hilt provider and binding modules.
- Coroutine dispatcher injection.
- Local-first data initialization.

## Data Flow

```text
DevJourneyApp.onCreate
   -> DemoDataSeeder.seedIfEmpty
   -> RoadmapDao.countRoadmaps
   -> FakeLearningApi.getLearningCatalog
   -> DTO mappers
   -> Room transaction
   -> DAO Flow
   -> Repository mapper
   -> Domain model
```

## Important Files

- `domain/model/LearningModels.kt`: app-facing models and enums.
- `domain/repository/Repositories.kt`: data contracts.
- `core/database/entity/Entities.kt`: Room table models.
- `core/database/dao/Daos.kt`: observable queries and writes.
- `core/database/DevJourneyDatabase.kt`: Room database definition.
- `core/network/FakeLearningApi.kt`: fake network boundary.
- `core/network/DemoLearningCatalog.kt`: deterministic seed content.
- `data/mapper/`: DTO/entity/domain conversions and calculations.
- `data/repository/DemoDataSeeder.kt`: first-run synchronization.
- `di/DatabaseModule.kt`, `NetworkModule.kt`, `RepositoryModule.kt`: Hilt wiring.

## Important Classes

- `DevJourneyDatabase`: owns the Room schema and DAO accessors.
- `DemoDataSeeder`: checks local state and writes the catalog transactionally.
- `InMemoryFakeLearningApi`: simulates an asynchronous API call.
- Repository implementations: translate storage streams and writes into domain behavior.
- `StringListConverter`: persists topic objectives in Room.

## Interview Notes

### Why make Room the source of truth?

Features can observe one consistent local state, work offline, and update reactively after writes. Network refresh can update Room without forcing UI to merge remote and local streams.

### Why map DTOs to entities?

Transport shape and database schema evolve independently. Mapping avoids coupling a future backend response directly to local storage.

### Why seed inside a transaction?

The catalog is related. A transaction prevents the app from observing a partially inserted demo data set.

### Is this a complete sync engine?

No. It is deterministic first-run seeding. Conflict resolution, retries, upload queues, pagination, and remote refresh policies are not implemented.

## Learning Checklist

- [x] Define Room entities and primary keys.
- [x] Write Flow-based DAO queries.
- [x] Use suspend functions for writes.
- [x] Export a Room schema.
- [x] Separate DTO, entity, and domain models.
- [x] Bind repository interfaces with Hilt.
- [x] Seed related data in a transaction.
- [ ] Add DAO and migration tests.

## Future Improvements

- Add Room DAO and repository integration tests.
- Add migration tests before schema version 2.
- Replace first-run-only seeding with explicit refresh metadata.
- Introduce a real API and sync/error policy.
- Normalize list persistence if objective querying becomes necessary.
