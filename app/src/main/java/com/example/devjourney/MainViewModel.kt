package com.example.devjourney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.model.UserSettings
import com.example.devjourney.domain.usecase.settings.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    val settings: StateFlow<UserSettings> = observeSettingsUseCase()
        .catch { emit(UserSettings()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings(),
        )
}
