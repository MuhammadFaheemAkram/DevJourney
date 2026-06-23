package co.bitfuse.devjourney.domain.usecase.search

import co.bitfuse.devjourney.domain.model.ChallengeStatus
import co.bitfuse.devjourney.domain.model.CodingChallenge
import co.bitfuse.devjourney.domain.model.Difficulty
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.model.ResourceType
import co.bitfuse.devjourney.domain.model.Roadmap
import co.bitfuse.devjourney.domain.model.RoadmapDetails
import co.bitfuse.devjourney.domain.model.Topic
import co.bitfuse.devjourney.domain.repository.ChallengesRepository
import co.bitfuse.devjourney.domain.repository.NotesRepository
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import co.bitfuse.devjourney.domain.repository.RoadmapRepository
import co.bitfuse.devjourney.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchEverythingUseCaseTest {
    private val useCase = SearchEverythingUseCase(
        roadmapRepository = FakeRoadmapRepository(),
        topicRepository = FakeTopicRepository(),
        notesRepository = FakeSearchNotesRepository(),
        resourcesRepository = FakeSearchResourcesRepository(),
        challengesRepository = FakeSearchChallengesRepository(),
    )

    @Test
    fun blankQuery_returnsEmptyResults() = runBlocking {
        val results = useCase("").first()

        assertTrue(results.isEmpty)
        assertEquals(0, results.totalCount)
    }

    @Test
    fun query_matchesAcrossAllSearchGroups() = runBlocking {
        val results = useCase("compose").first()

        assertEquals(listOf("roadmap-android"), results.roadmaps.map { it.id })
        assertEquals(listOf("topic-compose"), results.topics.map { it.id })
        assertEquals(listOf("note-compose"), results.notes.map { it.id })
        assertEquals(listOf("resource-compose"), results.resources.map { it.id })
        assertEquals(listOf("challenge-compose"), results.challenges.map { it.id })
        assertEquals(5, results.totalCount)
    }
}

private class FakeRoadmapRepository : RoadmapRepository {
    override fun observeRoadmaps(): Flow<List<Roadmap>> {
        return MutableStateFlow(
            listOf(
                Roadmap(
                    id = "roadmap-android",
                    title = "Android Compose",
                    description = "Build UI with Compose",
                    category = "Android",
                    completionPercentage = 0.5f,
                ),
            ),
        )
    }

    override fun observeRoadmapDetails(roadmapId: String): Flow<RoadmapDetails?> = emptyFlow()

    override suspend fun refreshRoadmaps() = Unit
}

private class FakeTopicRepository : TopicRepository {
    override fun observeTopics(roadmapId: String?): Flow<List<Topic>> {
        return MutableStateFlow(
            listOf(
                Topic(
                    id = "topic-compose",
                    roadmapId = "roadmap-android",
                    title = "Compose",
                    description = "Declarative UI",
                    difficulty = Difficulty.INTERMEDIATE,
                    estimatedMinutes = 120,
                    isCompleted = false,
                    isBookmarked = false,
                    sectionTitle = "UI",
                ),
            ),
        )
    }

    override fun observeTopic(topicId: String): Flow<Topic?> = emptyFlow()

    override suspend fun markTopicCompleted(topicId: String, isCompleted: Boolean) = Unit

    override suspend fun bookmarkTopic(topicId: String, isBookmarked: Boolean) = Unit
}

private class FakeSearchNotesRepository : NotesRepository {
    private val notes = MutableStateFlow(
        listOf(
            Note(
                id = "note-compose",
                topicId = "topic-compose",
                title = "Compose note",
                content = "State hoisting",
                updatedAt = 1L,
            ),
        ),
    )

    override fun observeNotes(): Flow<List<Note>> = notes

    override fun observeNotesForTopic(topicId: String): Flow<List<Note>> {
        return notes.map { noteList -> noteList.filter { it.topicId == topicId } }
    }

    override fun searchNotes(query: String): Flow<List<Note>> = observeNotes()

    override suspend fun createNote(note: Note) = Unit

    override suspend fun updateNote(note: Note) = Unit

    override suspend fun deleteNote(noteId: String) = Unit
}

private class FakeSearchResourcesRepository : ResourcesRepository {
    private val resources = MutableStateFlow(
        listOf(
            LearningResource(
                id = "resource-compose",
                topicId = "topic-compose",
                title = "Compose docs",
                url = "https://example.com/compose",
                type = ResourceType.DOCUMENTATION,
                source = "Example",
                description = "Compose documentation",
                isBookmarked = true,
            ),
        ),
    )

    override fun observeResources(): Flow<List<LearningResource>> = resources

    override fun searchResources(query: String): Flow<List<LearningResource>> = observeResources()

    override suspend fun bookmarkResource(resourceId: String, isBookmarked: Boolean) = Unit
}

private class FakeSearchChallengesRepository : ChallengesRepository {
    override fun observeChallenges(): Flow<List<CodingChallenge>> {
        return MutableStateFlow(
            listOf(
                CodingChallenge(
                    id = "challenge-compose",
                    title = "Compose challenge",
                    description = "Refactor state",
                    difficulty = Difficulty.INTERMEDIATE,
                    status = ChallengeStatus.NOT_STARTED,
                    topicId = "topic-compose",
                    solutionNotes = null,
                    updatedAt = 1L,
                ),
            ),
        )
    }

    override suspend fun completeChallenge(challengeId: String, solutionNotes: String?) = Unit
}
