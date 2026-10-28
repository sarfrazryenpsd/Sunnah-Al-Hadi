package com.ryen.sunnah_alhadi.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.CompositionContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.useCase.ExportAction
import com.ryen.sunnah_alhadi.domain.useCase.ExportParams
import com.ryen.sunnah_alhadi.domain.useCase.ExportResult
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportState {
    data object Idle : ExportState()
    data class Loading(val action: ExportAction) : ExportState()
    data class Success(val action: ExportAction, val uri: Uri?) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class ImageExportViewModel @Inject constructor(
    private val exportSunnahAsImageUseCase: ExportSunnahAsImageUseCase
) : ViewModel() {

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    /**
     * [context] must be the Activity context and [parentComposition] the caller's composition;
     * both are used only for the off-screen render and not retained.
     */
    fun exportSunnahAsImage(
        context: Context,
        parentComposition: CompositionContext,
        sunnah: Sunnah,
        action: ExportAction
    ) {
        if (_exportState.value is ExportState.Loading) return
        _exportState.value = ExportState.Loading(action)
        viewModelScope.launch {
            val params = ExportParams(context, parentComposition, sunnah, action)
            _exportState.value = when (val result = exportSunnahAsImageUseCase(params)) {
                is ExportResult.Success -> ExportState.Success(action, result.uri)
                is ExportResult.Error -> ExportState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _exportState.value = ExportState.Idle
    }
}
