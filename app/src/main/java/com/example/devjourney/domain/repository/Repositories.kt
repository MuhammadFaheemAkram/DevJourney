package com.example.devjourney.domain.repository

import com.example.devjourney.domain.model.CodingChallenge
import com.example.devjourney.domain.model.LearningAnalytics
import com.example.devjourney.domain.model.LearningGoal
import com.example.devjourney.domain.model.LearningResource
import com.example.devjourney.domain.model.Note
import com.example.devjourney.domain.model.Roadmap
import com.example.devjourney.domain.model.RoadmapDetails
import com.example.devjourney.domain.model.ThemePreference
import com.example.devjourney.domain.model.Topic
import com.example.devjourney.domain.model.TopicProgress
import com.example.devjourney.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    fun observeRoadmaps(): Flow<List<Roadmap>>
    fun observeRoadmapDetails(roadmapId: String): Flow<RoadmapDetails?>
    suspend fun refreshRoadmaps()
}

interface TopicRepository {
    fun observeTopics(roadmapId: String? = null): Flow<List<Topic>>
    fun observeTopic(topicId: String): Flow<Topic?>
    suspend fun markTopicCompleted(topicId: String, isCompleted: Boolean)
    suspend fun bookmarkTopic(topicId: String, isBookmarked: Boolean)
}

interface ProgressRepository {
    fun observeProgress(): Flow<List<TopicProgress>>
    suspend fun markTopicCompleted(topicId: String, isCompleted: Boolean)
}

interface NotesRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNotesForTopic(topicId: String): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun createNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
}

interface GoalsRepository {
    fun observeGoals(): Flow<List<LearningGoal>>
    suspend fun updateGoalProgress(goalId: String, currentCount: Int)
}

interface ChallengesRepository {
    fun observeChallenges(): Flow<List<CodingChallenge>>
    suspend fun completeChallenge(challengeId: String, solutionNotes: String?)
}

interface ResourcesRepository {
    fun observeResources(): Flow<List<LearningResource>>
    fun searchResources(query: String): Flow<List<LearningResource>>
    suspend fun bookmarkResource(resourceId: String, isBookmarked: Boolean)
}

interface AnalyticsRepository {
    fun observeAnalytics(): Flow<LearningAnalytics>
}

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun updateTheme(themePreference: ThemePreference)
    suspend fun updateDynamicColor(enabled: Boolean)
    suspend fun updateReminder(enabled: Boolean)
    suspend fun updateSelectedRoadmap(roadmapId: String?)
    suspend fun markFirstLaunchComplete()
}
