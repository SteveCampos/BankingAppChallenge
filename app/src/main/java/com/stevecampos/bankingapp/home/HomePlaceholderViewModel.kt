package com.stevecampos.bankingapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevecampos.domain.usecase.GetSessionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomePlaceholderState(
    val username: String = "",
    val accessTokenPreview: String = "",
)

@HiltViewModel
class HomePlaceholderViewModel @Inject constructor(
    private val getSessionStateUseCase: GetSessionStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomePlaceholderState())
    val state: StateFlow<HomePlaceholderState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val session = getSessionStateUseCase().session
            _state.value = HomePlaceholderState(
                username = session?.username.orEmpty(),
                accessTokenPreview = session?.accessToken?.takeLast(10).orEmpty(),
            )
        }
    }
}
