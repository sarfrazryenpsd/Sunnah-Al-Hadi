package com.ryen.sunnah_alhadi.presentation.viewmodel

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.MainDispatcherRule
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.useCase.ExportAction
import com.ryen.sunnah_alhadi.domain.useCase.ExportResult
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImageExportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: ExportSunnahAsImageUseCase = mockk()
    private val sunnah = Sunnah(id = "1", categoryId = 1, title = "Test", body = emptyList())

    private fun ImageExportViewModel.export(action: ExportAction) =
        exportSunnahAsImage(mockk(), mockk(), sunnah, action)

    @Test
    fun `save reports loading, then success with the saved uri`() = runTest {
        val uri: Uri = mockk()
        val result = CompletableDeferred<ExportResult>()
        coEvery { useCase(any()) } coAnswers { result.await() }
        val vm = ImageExportViewModel(useCase)

        vm.export(ExportAction.SAVE)
        advanceUntilIdle()
        assertThat(vm.exportState.value).isEqualTo(ExportState.Loading(ExportAction.SAVE))

        result.complete(ExportResult.Success(uri))
        advanceUntilIdle()
        assertThat(vm.exportState.value).isEqualTo(ExportState.Success(ExportAction.SAVE, uri))
        coVerify { useCase(match { it.action == ExportAction.SAVE && it.sunnah == sunnah }) }
    }

    @Test
    fun `failure is reported as an error state`() = runTest {
        coEvery { useCase(any()) } returns ExportResult.Error("disk full")
        val vm = ImageExportViewModel(useCase)

        vm.export(ExportAction.SHARE)
        advanceUntilIdle()

        assertThat(vm.exportState.value).isEqualTo(ExportState.Error("disk full"))
    }

    @Test
    fun `a second tap while exporting is ignored`() = runTest {
        val result = CompletableDeferred<ExportResult>()
        coEvery { useCase(any()) } coAnswers { result.await() }
        val vm = ImageExportViewModel(useCase)

        vm.export(ExportAction.SAVE)
        advanceUntilIdle()
        vm.export(ExportAction.SHARE)
        result.complete(ExportResult.Success(null))
        advanceUntilIdle()

        coVerify(exactly = 1) { useCase(any()) }
        assertThat(vm.exportState.value).isEqualTo(ExportState.Success(ExportAction.SAVE, null))
    }
}
