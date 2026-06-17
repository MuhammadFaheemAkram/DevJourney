package com.example.devjourney.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.usecase.search.SearchEverythingResults
import com.example.devjourney.domain.usecase.search.SearchEverythingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class SearchUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val results: SearchEverythingResults = SearchEverythingResults(),
    val errorMessage: String? = null,
) {
    val hasQuery: Boolean = query.isNotBlank()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    searchEverythingUseCase: SearchEverythingUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val debouncedResults = query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { currentQuery ->
            searchEverythingUseCase(currentQuery)
                .map { results -> currentQuery to results }
        }
        .onStart { emit("" to SearchEverythingResults()) }

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        debouncedResults,
    ) { currentQuery, (searchedQuery, results) ->
        SearchUiState(
            isLoading = false,
            query = currentQuery,
            results = if (currentQuery == searchedQuery) results else SearchEverythingResults(),
        )
    }
        .catch { error ->
            emit(
                SearchUiState(
                    isLoading = false,
                    query = query.value,
                    errorMessage = error.message ?: "Unable to search DevJourney.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(),
        )

    fun onQueryChanged(value: String) {
        query.value = value
    }
}
