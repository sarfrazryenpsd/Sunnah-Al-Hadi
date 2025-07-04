package com.ryen.sunnah_alhadi.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class ResultTest {

    @Test
    fun result_success_should_contain_correct_data() {
        // Given
        val testData = "Test Data"

        // When
        val result = Result.Success(testData)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.data).isEqualTo(testData)
    }

    @Test
    fun result_success_should_handle_null_data() {
        // Given
        val testData: String? = null

        // When
        val result = Result.Success(testData)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.data).isNull()
    }

    @Test
    fun result_success_should_handle_complex_objects() {
        // Given
        val testData = listOf("item1", "item2", "item3")

        // When
        val result = Result.Success(testData)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.data).hasSize(3)
        assertThat(result.data).containsExactly("item1", "item2", "item3")
    }

    @Test
    fun result_error_should_contain_exception_and_message() {
        // Given
        val exception = RuntimeException("Test exception")
        val message = "Test error message"

        // When
        val result = Result.Error(exception, message)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.exception).isEqualTo(exception)
        assertThat(result.message).isEqualTo(message)
    }

    @Test
    fun result_error_should_handle_null_message() {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val result = Result.Error(exception)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.exception).isEqualTo(exception)
        assertThat(result.message).isNull()
    }

    @Test
    fun result_error_should_handle_different_exception_types() {
        // Given
        val ioException = IOException("IO Exception")
        val illegalArgumentException = IllegalArgumentException("Illegal argument")
        val securityException = SecurityException("Security exception")

        // When
        val ioResult = Result.Error(ioException, "IO Error")
        val illegalResult = Result.Error(illegalArgumentException, "Illegal Error")
        val securityResult = Result.Error(securityException, "Security Error")

        // Then
        assertThat(ioResult.exception).isInstanceOf(IOException::class.java)
        assertThat(illegalResult.exception).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(securityResult.exception).isInstanceOf(SecurityException::class.java)
    }


    @Test
    fun result_should_handle_equality_comparison() {
        // Given
        val success1 = Result.Success("test")
        val success2 = Result.Success("test")
        val success3 = Result.Success("different")

        val error1 = Result.Error(RuntimeException("error"), "message")
        val error2 = Result.Error(RuntimeException("error"), "message")

        // When/Then
        assertThat(success1).isEqualTo(success2)
        assertThat(success1).isNotEqualTo(success3)
        assertThat(success1).isNotEqualTo(error1)

        // Note: Error equality depends on exception instance, not just message
        assertThat(error1).isNotEqualTo(error2) // Different exception instances
    }

    @Test
    fun result_should_handle_hashcode_consistently() {
        // Given
        val success1 = Result.Success("test")
        val success2 = Result.Success("test")

        // When
        val hashCode1 = success1.hashCode()
        val hashCode2 = success2.hashCode()

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2)
    }

    @Test
    fun result_should_handle_toString_formatting() {
        // Given
        val success = Result.Success("test data")
        val error = Result.Error(RuntimeException("test error"), "Error message")

        // When
        val successString = success.toString()
        val errorString = error.toString()

        // Then
        assertThat(successString).contains("Success")
        assertThat(successString).contains("test data")
        assertThat(errorString).contains("Error")
        assertThat(errorString).contains("Error message")
    }
}