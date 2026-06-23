package co.bitfuse.devjourney.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.bitfuse.devjourney.core.database.converter.StringListConverter
import co.bitfuse.devjourney.core.database.dao.BookmarkDao
import co.bitfuse.devjourney.core.database.dao.ChallengeDao
import co.bitfuse.devjourney.core.database.dao.GoalDao
import co.bitfuse.devjourney.core.database.dao.NoteDao
import co.bitfuse.devjourney.core.database.dao.ProgressDao
import co.bitfuse.devjourney.core.database.dao.ResourceDao
import co.bitfuse.devjourney.core.database.dao.RoadmapDao
import co.bitfuse.devjourney.core.database.dao.TopicDao
import co.bitfuse.devjourney.core.database.entity.BookmarkEntity
import co.bitfuse.devjourney.core.database.entity.ChallengeEntity
import co.bitfuse.devjourney.core.database.entity.GoalEntity
import co.bitfuse.devjourney.core.database.entity.NoteEntity
import co.bitfuse.devjourney.core.database.entity.ProgressEntity
import co.bitfuse.devjourney.core.database.entity.ResourceEntity
import co.bitfuse.devjourney.core.database.entity.RoadmapEntity
import co.bitfuse.devjourney.core.database.entity.TopicEntity

@Database(
    entities = [
        RoadmapEntity::class,
        TopicEntity::class,
        ProgressEntity::class,
        NoteEntity::class,
        GoalEntity::class,
        ChallengeEntity::class,
        ResourceEntity::class,
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class DevJourneyDatabase : RoomDatabase() {
    abstract fun roadmapDao(): RoadmapDao
    abstract fun topicDao(): TopicDao
    abstract fun progressDao(): ProgressDao
    abstract fun noteDao(): NoteDao
    abstract fun goalDao(): GoalDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun resourceDao(): ResourceDao
    abstract fun bookmarkDao(): BookmarkDao
}
