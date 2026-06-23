package co.bitfuse.devjourney.feature.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.model.ResourceType
import co.bitfuse.devjourney.domain.usecase.resources.BookmarkResourceUseCase
import co.bitfuse.devjourney.domain.usecase.resources.SearchResourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ResourcesUiState(
    val isLoading: Boolean = true,
    val resources: List<LearningResource> = emptyList(),
    val searchQuery: String = "",
    val selectedType: String = ALL_TYPES,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && resources.isEmpty()
}

sealed interface ResourcesEffect {
    data class BookmarkChanged(val title: String, val isBookmarked: Boolean) : ResourcesEffect
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ResourcesViewModel @Inject constructor(
    searchResourcesUseCase: SearchResourcesUseCase,
    private val bookmarkResourceUseCase: BookmarkResourceUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedType = MutableStateFlow(ALL_TYPES)
    private val _effects = MutableSharedFlow<ResourcesEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ResourcesUiState> = combine(
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query -> searchResourcesUseCase(query) },
        searchQuery,
        selectedType,
    ) { resources, query, type ->
        ResourcesUiState(
            isLoading = false,
            resources = resources.filter { resource -> type == ALL_TYPES || resource.type.displayName == type },
            searchQuery = query,
            selectedType = type,
        )
    }.catch { error ->
        emit(
            ResourcesUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load resources.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ResourcesUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onTypeSelected(type: String) {
        selectedType.value = type
    }

    fun onBookmarkClick(resource: LearningResource) {
        viewModelScope.launch {
            val newBookmarkState = !resource.isBookmarked
            bookmarkResourceUseCase(resource.id, newBookmarkState)
            _effects.emit(ResourcesEffect.BookmarkChanged(resource.title, newBookmarkState))
        }
    }
}

val ResourceTypeFilters = listOf(ALL_TYPES) + ResourceType.entries.map { it.displayName }

val ResourceType.displayName: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

private const val ALL_TYPES = "All"
