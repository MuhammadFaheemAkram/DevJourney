package com.example.devjourney.feature.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.model.Roadmap
import com.example.devjourney.domain.usecase.roadmap.ObserveRoadmapsUseCase
import com.example.devjourney.domain.usecase.roadmap.RefreshRoadmapsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RoadmapsUiState(
    val isLoading: Boolean = true,
    val roadmaps: List<Roadmap> = emptyList(),
    val categories: List<String> = listOf(ALL_CATEGORIES),
    val searchQuery: String = "",
    val selectedCategory: String = ALL_CATEGORIES,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && roadmaps.isEmpty()
}

@HiltViewModel
class RoadmapsViewModel @Inject constructor(
    observeRoadmapsUseCase: ObserveRoadmapsUseCase,
    private val refreshRoadmapsUseCase: RefreshRoadmapsUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(ALL_CATEGORIES)

    val uiState: StateFlow<RoadmapsUiState> = combine(
        observeRoadmapsUseCase(),
        searchQuery,
        selectedCategory,
    ) { roadmaps, query, category ->
        val categories = listOf(ALL_CATEGORIES) + roadmaps.map { it.category }.distinct()
        val filteredRoadmaps = roadmaps.filter { roadmap ->
            val matchesCategory = category == ALL_CATEGORIES || roadmap.category == category
            val matchesQuery = query.isBlank() ||
                roadmap.title.contains(query, ignoreCase = true) ||
                roadmap.description.contains(query, ignoreCase = true) ||
                roadmap.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        RoadmapsUiState(
            isLoading = false,
            roadmaps = filteredRoadmaps,
            categories = categories,
            searchQuery = query,
            selectedCategory = category.takeIf { it in categories } ?: ALL_CATEGORIES,
        )
    }.catch { error ->
        emit(
            RoadmapsUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load roadmaps.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoadmapsUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    fun refresh() {
        viewModelScope.launch {
            refreshRoadmapsUseCase()
        }
    }
}

private const val ALL_CATEGORIES = "All"
