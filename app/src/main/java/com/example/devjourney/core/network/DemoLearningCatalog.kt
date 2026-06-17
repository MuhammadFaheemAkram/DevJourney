package com.example.devjourney.core.network

import com.example.devjourney.core.network.dto.BookmarkDto
import com.example.devjourney.core.network.dto.ChallengeDto
import com.example.devjourney.core.network.dto.GoalDto
import com.example.devjourney.core.network.dto.LearningCatalogDto
import com.example.devjourney.core.network.dto.NoteDto
import com.example.devjourney.core.network.dto.ProgressDto
import com.example.devjourney.core.network.dto.ResourceDto
import com.example.devjourney.core.network.dto.RoadmapDto
import com.example.devjourney.core.network.dto.TopicDto
import java.util.concurrent.TimeUnit

object DemoLearningCatalog {
    fun create(now: Long = System.currentTimeMillis()): LearningCatalogDto {
        val roadmaps = listOf(
            RoadmapDto(
                id = "roadmap-android",
                title = "Android Developer",
                description = "Build production Android apps with Kotlin, Compose, offline data, DI, and testing.",
                category = "Android",
                estimatedHours = 96,
                sortOrder = 0,
            ),
            RoadmapDto(
                id = "roadmap-backend",
                title = "Backend Developer",
                description = "Design APIs, persist data, containerize services, and ship backend systems.",
                category = "Backend",
                estimatedHours = 72,
                sortOrder = 1,
            ),
            RoadmapDto(
                id = "roadmap-ai",
                title = "AI Engineer",
                description = "Learn prompt design, RAG, vector databases, orchestration, MCP, and agent patterns.",
                category = "AI Engineering",
                estimatedHours = 84,
                sortOrder = 2,
            ),
            RoadmapDto(
                id = "roadmap-system-design",
                title = "System Design",
                description = "Reason about scalability, caching, queues, load balancing, and resilient systems.",
                category = "System Design",
                estimatedHours = 64,
                sortOrder = 3,
            ),
        )

        val topics = buildList {
            addTopic(
                id = "topic-kotlin",
                roadmapId = "roadmap-android",
                sectionTitle = "Language foundations",
                title = "Kotlin Fundamentals",
                description = "Types, null safety, functions, collections, classes, and idiomatic Kotlin.",
                difficulty = "BEGINNER",
                estimatedMinutes = 180,
                objectives = listOf("Use null safety", "Model data with classes", "Transform collections"),
                sortOrder = 0,
            )
            addTopic(
                id = "topic-coroutines",
                roadmapId = "roadmap-android",
                sectionTitle = "Async programming",
                title = "Coroutines",
                description = "Structured concurrency, dispatchers, scopes, jobs, and cancellation.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 210,
                objectives = listOf("Launch coroutine scopes", "Handle cancellation", "Switch dispatchers safely"),
                sortOrder = 1,
            )
            addTopic(
                id = "topic-flow",
                roadmapId = "roadmap-android",
                sectionTitle = "Async programming",
                title = "Flow",
                description = "Cold streams, operators, backpressure, collection, and lifecycle-aware observation.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 180,
                objectives = listOf("Expose streams with Flow", "Transform emissions", "Collect with lifecycle"),
                sortOrder = 2,
            )
            addTopic(
                id = "topic-stateflow",
                roadmapId = "roadmap-android",
                sectionTitle = "State management",
                title = "StateFlow",
                description = "Immutable UI state, state reducers, one-time effects, and state sharing.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 150,
                objectives = listOf("Expose immutable state", "Model loading states", "Avoid business logic in UI"),
                sortOrder = 3,
            )
            addTopic(
                id = "topic-compose",
                roadmapId = "roadmap-android",
                sectionTitle = "UI",
                title = "Compose",
                description = "Declarative UI, stateless composables, state hoisting, layouts, and Material 3.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 240,
                objectives = listOf("Build stateless UI", "Use Material 3", "Design reusable components"),
                sortOrder = 4,
            )
            addTopic(
                id = "topic-navigation",
                roadmapId = "roadmap-android",
                sectionTitle = "App architecture",
                title = "Navigation",
                description = "Navigation Compose graphs, destinations, arguments, and app-level navigation ownership.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 120,
                objectives = listOf("Create navigation graphs", "Navigate single-top", "Keep screens stateless"),
                sortOrder = 5,
            )
            addTopic(
                id = "topic-room",
                roadmapId = "roadmap-android",
                sectionTitle = "Persistence",
                title = "Room",
                description = "Entities, DAOs, migrations, transactions, and observable offline-first data.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 210,
                objectives = listOf("Model entities", "Write DAO queries", "Observe database changes"),
                sortOrder = 6,
            )
            addTopic(
                id = "topic-hilt",
                roadmapId = "roadmap-android",
                sectionTitle = "App architecture",
                title = "Hilt",
                description = "Dependency injection, modules, scopes, bindings, and Android entry points.",
                difficulty = "INTERMEDIATE",
                estimatedMinutes = 150,
                objectives = listOf("Bind interfaces", "Provide database dependencies", "Scope app services"),
                sortOrder = 7,
            )
            addTopic(
                id = "topic-testing",
                roadmapId = "roadmap-android",
                sectionTitle = "Quality",
                title = "Testing",
                description = "Unit tests, fake repositories, Flow testing, Room tests, and Compose UI tests.",
                difficulty = "ADVANCED",
                estimatedMinutes = 240,
                objectives = listOf("Test business logic", "Assert Flow emissions", "Verify Compose behavior"),
                sortOrder = 8,
            )
            addTopic("topic-python", "roadmap-backend", "Language", "Python", "Syntax, data structures, modules, typing, and testing.", "BEGINNER", 180, listOf("Write typed functions", "Use virtual environments", "Structure modules"), 0)
            addTopic("topic-fastapi", "roadmap-backend", "API development", "FastAPI", "REST APIs, validation, dependency injection, and async handlers.", "INTERMEDIATE", 180, listOf("Create routes", "Validate schemas", "Handle errors"), 1)
            addTopic("topic-postgresql", "roadmap-backend", "Persistence", "PostgreSQL", "Relational modeling, indexing, joins, transactions, and query planning.", "INTERMEDIATE", 210, listOf("Design tables", "Write joins", "Use indexes"), 2)
            addTopic("topic-docker", "roadmap-backend", "Delivery", "Docker", "Images, containers, Compose files, local environments, and deployment basics.", "INTERMEDIATE", 150, listOf("Build images", "Run containers", "Compose services"), 3)
            addTopic("topic-prompt-engineering", "roadmap-ai", "Foundations", "Prompt Engineering", "Instruction design, evaluation, system prompts, and prompt iteration.", "BEGINNER", 120, listOf("Write clear instructions", "Design evaluations", "Reduce ambiguity"), 0)
            addTopic("topic-rag", "roadmap-ai", "Retrieval", "RAG", "Retrieval augmented generation, chunking, embeddings, ranking, and answer grounding.", "INTERMEDIATE", 210, listOf("Chunk documents", "Retrieve context", "Ground answers"), 1)
            addTopic("topic-vector-databases", "roadmap-ai", "Retrieval", "Vector Databases", "Embedding storage, similarity search, metadata filters, and retrieval tradeoffs.", "INTERMEDIATE", 180, listOf("Store vectors", "Filter metadata", "Tune retrieval"), 2)
            addTopic("topic-langchain", "roadmap-ai", "Orchestration", "LangChain", "Chains, tools, retrievers, agents, and orchestration concepts.", "INTERMEDIATE", 150, listOf("Compose chains", "Use retrievers", "Evaluate abstractions"), 3)
            addTopic("topic-mcp", "roadmap-ai", "Tooling", "MCP", "Model Context Protocol servers, tools, resources, and integration boundaries.", "ADVANCED", 180, listOf("Expose tools", "Read resources", "Design safe integrations"), 4)
            addTopic("topic-ai-agents", "roadmap-ai", "Agent systems", "AI Agents", "Planning loops, tool use, memory, evaluation, and operational guardrails.", "ADVANCED", 240, listOf("Model agent loops", "Add tool use", "Evaluate behavior"), 5)
            addTopic("topic-scalability", "roadmap-system-design", "Foundations", "Scalability", "Horizontal scaling, vertical scaling, partitioning, and capacity planning.", "INTERMEDIATE", 180, listOf("Estimate load", "Choose scaling strategies", "Identify bottlenecks"), 0)
            addTopic("topic-caching", "roadmap-system-design", "Performance", "Caching", "Cache layers, invalidation, TTLs, consistency, and performance tradeoffs.", "INTERMEDIATE", 150, listOf("Pick cache layers", "Handle invalidation", "Define TTLs"), 1)
            addTopic("topic-queues", "roadmap-system-design", "Async systems", "Queues", "Message queues, retries, dead letter queues, idempotency, and backpressure.", "INTERMEDIATE", 180, listOf("Use queues", "Design retries", "Protect consumers"), 2)
            addTopic("topic-load-balancers", "roadmap-system-design", "Traffic", "Load Balancers", "Traffic distribution, health checks, routing, failover, and regional design.", "INTERMEDIATE", 120, listOf("Route traffic", "Add health checks", "Plan failover"), 3)
        }

        val oneDay = TimeUnit.DAYS.toMillis(1)
        val progress = listOf(
            ProgressDto("topic-kotlin", "roadmap-android", true, now - oneDay * 6, now - oneDay * 6),
            ProgressDto("topic-coroutines", "roadmap-android", true, now - oneDay * 5, now - oneDay * 5),
            ProgressDto("topic-flow", "roadmap-android", true, now - oneDay * 4, now - oneDay * 4),
            ProgressDto("topic-stateflow", "roadmap-android", false, null, now - oneDay),
            ProgressDto("topic-compose", "roadmap-android", true, now, now),
            ProgressDto("topic-python", "roadmap-backend", true, now - oneDay * 3, now - oneDay * 3),
            ProgressDto("topic-prompt-engineering", "roadmap-ai", true, now - oneDay * 2, now - oneDay * 2),
            ProgressDto("topic-caching", "roadmap-system-design", true, now - oneDay, now - oneDay),
        )

        val notes = listOf(
            NoteDto(
                id = "note-compose-state",
                topicId = "topic-compose",
                title = "State hoisting rule",
                content = "Keep source of truth above reusable composables and pass events upward.",
                updatedAt = now - TimeUnit.HOURS.toMillis(4),
            ),
            NoteDto(
                id = "note-rag-eval",
                topicId = "topic-rag",
                title = "RAG evaluation",
                content = "Track retrieval precision separately from answer quality.",
                updatedAt = now - TimeUnit.HOURS.toMillis(12),
            ),
        )

        val goals = listOf(
            GoalDto("goal-daily-topics", "Complete 2 learning topics", "DAILY", 2, 1, now - oneDay, now + oneDay),
            GoalDto("goal-weekly-notes", "Write 5 study notes", "WEEKLY", 5, 2, now - oneDay * 2, now + oneDay * 5),
            GoalDto("goal-monthly-challenges", "Solve 8 coding challenges", "MONTHLY", 8, 3, now - oneDay * 10, now + oneDay * 20),
        )

        val challenges = listOf(
            ChallengeDto("challenge-kotlin-collections", "topic-kotlin", "Kotlin collection transforms", "Use map, filter, groupBy, and fold to summarize learning data.", "BEGINNER", "COMPLETED", "Prefer small pure functions for transform steps.", now - oneDay * 2),
            ChallengeDto("challenge-compose-state", "topic-compose", "Compose state hoisting", "Refactor a stateful form into stateless composables and event callbacks.", "INTERMEDIATE", "IN_PROGRESS", null, now - oneDay),
            ChallengeDto("challenge-sql-index", "topic-postgresql", "PostgreSQL indexing", "Choose indexes for a query-heavy notes table.", "INTERMEDIATE", "NOT_STARTED", null, now),
            ChallengeDto("challenge-cache-design", "topic-caching", "Feed cache strategy", "Design cache invalidation for a high traffic learning feed.", "ADVANCED", "NOT_STARTED", null, now),
        )

        val resources = listOf(
            ResourceDto("resource-kotlin-docs", "topic-kotlin", "Kotlin language tour", "https://kotlinlang.org/docs/kotlin-tour-welcome.html", "DOCUMENTATION", "Kotlin", "Official Kotlin language overview."),
            ResourceDto("resource-compose-pathway", "topic-compose", "Jetpack Compose pathway", "https://developer.android.com/courses/jetpack-compose/course", "COURSE", "Android Developers", "Official Compose course and examples."),
            ResourceDto("resource-room-guide", "topic-room", "Save data with Room", "https://developer.android.com/training/data-storage/room", "DOCUMENTATION", "Android Developers", "Room persistence library guide."),
            ResourceDto("resource-fastapi-docs", "topic-fastapi", "FastAPI documentation", "https://fastapi.tiangolo.com/", "DOCUMENTATION", "FastAPI", "Official FastAPI documentation."),
            ResourceDto("resource-rag-patterns", "topic-rag", "RAG architecture notes", "https://www.promptingguide.ai/research/rag", "ARTICLE", "Prompting Guide", "Conceptual RAG patterns and tradeoffs."),
            ResourceDto("resource-system-design-primer", "topic-scalability", "System design primer", "https://github.com/donnemartin/system-design-primer", "ARTICLE", "Open Source", "Foundational system design reference."),
        )

        val bookmarks = listOf(
            BookmarkDto("topic-compose", "TOPIC", now - oneDay),
            BookmarkDto("resource-compose-pathway", "RESOURCE", now - oneDay),
            BookmarkDto("resource-system-design-primer", "RESOURCE", now - TimeUnit.HOURS.toMillis(8)),
        )

        return LearningCatalogDto(
            roadmaps = roadmaps,
            topics = topics,
            notes = notes,
            goals = goals,
            challenges = challenges,
            resources = resources,
            progress = progress,
            bookmarks = bookmarks,
        )
    }

    private fun MutableList<TopicDto>.addTopic(
        id: String,
        roadmapId: String,
        sectionTitle: String,
        title: String,
        description: String,
        difficulty: String,
        estimatedMinutes: Int,
        objectives: List<String>,
        sortOrder: Int,
    ) {
        add(
            TopicDto(
                id = id,
                roadmapId = roadmapId,
                sectionTitle = sectionTitle,
                title = title,
                description = description,
                difficulty = difficulty,
                estimatedMinutes = estimatedMinutes,
                objectives = objectives,
                sortOrder = sortOrder,
            ),
        )
    }
}
