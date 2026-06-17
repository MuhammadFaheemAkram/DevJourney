package com.example.devjourney.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.model.LearningAnalytics
import com.example.devjourney.domain.usecase.analytics.ObserveAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: LearningAnalytics = LearningAnalytics(
        streakDays = 0,
        completedTopics = 0,
        totalTopics = 0,
        completionRate = 0f,
    ),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && analytics.totalTopics == 0
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    observeAnalyticsUseCase: ObserveAnalyticsUseCase,
) : ViewModel() {
    val uiState: StateFlow<AnalyticsUiState> = observeAnalyticsUseCase()
        .map { analytics ->
            AnalyticsUiState(
                isLoading = false,
                analytics = analytics,
            )
        }
        .catch { error ->
            emit(
                AnalyticsUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Unable to load analytics.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalyticsUiState(),
        )
}
