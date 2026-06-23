package co.bitfuse.devjourney.data.resources

import co.bitfuse.devjourney.core.database.dao.BookmarkDao
import co.bitfuse.devjourney.core.database.dao.ResourceDao
import co.bitfuse.devjourney.core.database.entity.BookmarkEntity
import co.bitfuse.devjourney.data.mapper.toDomain
import co.bitfuse.devjourney.domain.model.BookmarkTargetType
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ResourcesRepositoryImpl @Inject constructor(
    private val resourceDao: ResourceDao,
    private val bookmarkDao: BookmarkDao,
) : ResourcesRepository {
    override fun observeResources(): Flow<List<LearningResource>> {
        return combine(
            resourceDao.observeResources(),
            bookmarkDao.observeBookmarks(BookmarkTargetType.RESOURCE.name),
        ) { resources, bookmarks ->
            val bookmarkedResourceIds = bookmarks.mapTo(mutableSetOf()) { it.targetId }
            resources.map { resource ->
                resource.toDomain(isBookmarked = resource.id in bookmarkedResourceIds)
            }
        }
    }

    override fun searchResources(query: String): Flow<List<LearningResource>> {
        return combine(
            resourceDao.searchResources(query),
            bookmarkDao.observeBookmarks(BookmarkTargetType.RESOURCE.name),
        ) { resources, bookmarks ->
            val bookmarkedResourceIds = bookmarks.mapTo(mutableSetOf()) { it.targetId }
            resources.map { resource ->
                resource.toDomain(isBookmarked = resource.id in bookmarkedResourceIds)
            }
        }
    }

    override suspend fun bookmarkResource(resourceId: String, isBookmarked: Boolean) {
        if (isBookmarked) {
            bookmarkDao.upsertBookmark(
                BookmarkEntity(
                    targetId = resourceId,
                    targetType = BookmarkTargetType.RESOURCE.name,
                    createdAt = System.currentTimeMillis(),
                ),
            )
        } else {
            bookmarkDao.deleteBookmark(
                targetId = resourceId,
                targetType = BookmarkTargetType.RESOURCE.name,
            )
        }
    }
}
