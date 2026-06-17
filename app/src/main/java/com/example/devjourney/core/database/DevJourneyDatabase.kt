package com.example.devjourney.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.devjourney.core.database.converter.StringListConverter
import com.example.devjourney.core.database.dao.BookmarkDao
import com.example.devjourney.core.database.dao.ChallengeDao
import com.example.devjourney.core.database.dao.GoalDao
import com.example.devjourney.core.database.dao.NoteDao
import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.ResourceDao
import com.example.devjourney.core.database.dao.RoadmapDao
import com.example.devjourney.core.database.dao.TopicDao
import com.example.devjourney.core.database.entity.BookmarkEntity
import com.example.devjourney.core.database.entity.ChallengeEntity
import com.example.devjourney.core.database.entity.GoalEntity
import com.example.devjourney.core.database.entity.NoteEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.core.database.entity.ResourceEntity
import com.example.devjourney.core.database.entity.RoadmapEntity
import com.example.devjourney.core.database.entity.TopicEntity

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
