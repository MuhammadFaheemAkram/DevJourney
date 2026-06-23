package co.bitfuse.devjourney.domain.usecase.search

import co.bitfuse.devjourney.domain.repository.ChallengesRepository
import co.bitfuse.devjourney.domain.repository.NotesRepository
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import co.bitfuse.devjourney.domain.repository.RoadmapRepository
import co.bitfuse.devjourney.domain.repository.TopicRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class SearchEverythingResults(
    val roadmaps: List<SearchResultItem> = emptyList(),
    val topics: List<SearchResultItem> = emptyList(),
    val notes: List<SearchResultItem> = emptyList(),
    val resources: List<SearchResultItem> = emptyList(),
    val challenges: List<SearchResultItem> = emptyList(),
) {
    val totalCount: Int = roadmaps.size + topics.size + notes.size + resources.size + challenges.size
    val isEmpty: Boolean = totalCount == 0
}

data class SearchResultItem(
    val id: String,
    val title: String,
    val description: String,
    val metadata: String,
)

class SearchEverythingUseCase @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
    private val topicRepository: TopicRepository,
    private val notesRepository: NotesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val challengesRepository: ChallengesRepository,
) {
    operator fun invoke(query: String): Flow<SearchEverythingResults> {
        val normalizedQuery = query.trim()
        return combine(
            roadmapRepository.observeRoadmaps(),
            topicRepository.observeTopics(),
            notesRepository.observeNotes(),
            resourcesRepository.observeResources(),
            challengesRepository.observeChallenges(),
        ) { roadmaps, topics, notes, resources, challenges ->
            if (normalizedQuery.isBlank()) {
                SearchEverythingResults()
            } else {
                SearchEverythingResults(
                    roadmaps = roadmaps
                        .filter { it.title.matchesQuery(normalizedQuery) || it.description.matchesQuery(normalizedQuery) || it.category.matchesQuery(normalizedQuery) }
                        .map {
                            SearchResultItem(
                                id = it.id,
                                title = it.title,
                                description = it.description,
                                metadata = "${it.category} roadmap",
                            )
                        },
                    topics = topics
                        .filter { it.title.matchesQuery(normalizedQuery) || it.description.matchesQuery(normalizedQuery) || it.sectionTitle.matchesQuery(normalizedQuery) }
                        .map {
                            SearchResultItem(
                                id = it.id,
                                title = it.title,
                                description = it.description,
                                metadata = "${it.sectionTitle} topic",
                            )
                        },
                    notes = notes
                        .filter { it.title.matchesQuery(normalizedQuery) || it.content.matchesQuery(normalizedQuery) }
                        .map {
                            SearchResultItem(
                                id = it.id,
                                title = it.title,
                                description = it.content,
                                metadata = "Note",
                            )
                        },
                    resources = resources
                        .filter { it.title.matchesQuery(normalizedQuery) || it.description.matchesQuery(normalizedQuery) || it.source.matchesQuery(normalizedQuery) }
                        .map {
                            SearchResultItem(
                                id = it.id,
                                title = it.title,
                                description = it.description,
                                metadata = "${it.type.name.lowercase().replaceFirstChar { char -> char.uppercase() }} - ${it.source}",
                            )
                        },
                    challenges = challenges
                        .filter { it.title.matchesQuery(normalizedQuery) || it.description.matchesQuery(normalizedQuery) || it.difficulty.name.matchesQuery(normalizedQuery) }
                        .map {
                            SearchResultItem(
                                id = it.id,
                                title = it.title,
                                description = it.description,
                                metadata = "${it.difficulty.name.lowercase().replaceFirstChar { char -> char.uppercase() }} challenge",
                            )
                        },
                )
            }
        }
    }
}

private fun String.matchesQuery(query: String): Boolean {
    return contains(query, ignoreCase = true)
}
