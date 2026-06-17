package com.example.devjourney.domain.usecase.resources

import com.example.devjourney.domain.model.LearningResource
import com.example.devjourney.domain.model.ResourceType
import com.example.devjourney.domain.repository.ResourcesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceUseCasesTest {
    private val repository = FakeResourcesRepository(
        initialResources = listOf(
            resource(id = "compose", title = "Compose pathway", description = "UI course", isBookmarked = false),
            resource(id = "room", title = "Room guide", description = "Persistence docs", isBookmarked = true),
        ),
    )

    @Test
    fun searchResources_blankQueryReturnsAllResources() = runBlocking {
        val useCase = SearchResourcesUseCase(repository)

        val resources = useCase("").first()

        assertEquals(listOf("compose", "room"), resources.map { it.id })
    }

    @Test
    fun searchResources_filtersByTitleAndDescription() = runBlocking {
        val useCase = SearchResourcesUseCase(repository)

        val resources = useCase("persistence").first()

        assertEquals(listOf("room"), resources.map { it.id })
    }

    @Test
    fun bookmarkResource_updatesBookmarkState() = runBlocking {
        val useCase = BookmarkResourceUseCase(repository)

        useCase("compose", true)

        val resource = repository.observeResources().first().first { it.id == "compose" }

        assertTrue(resource.isBookmarked)
    }

    @Test
    fun bookmarkResource_canRemoveBookmark() = runBlocking {
        val useCase = BookmarkResourceUseCase(repository)

        useCase("room", false)

        val resource = repository.observeResources().first().first { it.id == "room" }

        assertFalse(resource.isBookmarked)
    }

    private fun resource(
        id: String,
        title: String,
        description: String,
        isBookmarked: Boolean,
    ): LearningResource {
        return LearningResource(
            id = id,
            topicId = null,
            title = title,
            url = "https://example.com/$id",
            type = ResourceType.DOCUMENTATION,
            source = "Example",
            description = description,
            isBookmarked = isBookmarked,
        )
    }
}

private class FakeResourcesRepository(
    initialResources: List<LearningResource>,
) : ResourcesRepository {
    private val resources = MutableStateFlow(initialResources)

    override fun observeResources(): Flow<List<LearningResource>> = resources

    override fun searchResources(query: String): Flow<List<LearningResource>> {
        return resources.map { resourceList ->
            resourceList.filter { resource ->
                resource.title.contains(query, ignoreCase = true) ||
                    resource.description.contains(query, ignoreCase = true) ||
                    resource.source.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun bookmarkResource(resourceId: String, isBookmarked: Boolean) {
        resources.value = resources.value.map { resource ->
            if (resource.id == resourceId) resource.copy(isBookmarked = isBookmarked) else resource
        }
    }
}
