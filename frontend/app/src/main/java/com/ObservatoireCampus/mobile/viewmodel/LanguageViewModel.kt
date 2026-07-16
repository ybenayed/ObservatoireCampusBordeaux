package com.ObservatoireCampus.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class AppLanguage { FR, EN, AR }

private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { result -> cont.resume(result) }
    addOnFailureListener { exception -> cont.resumeWithException(exception) }
}

class LanguageViewModel : ViewModel() {

    private val _currentLanguage = MutableStateFlow(AppLanguage.FR)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val translators = mutableMapOf<AppLanguage, Translator>()

    private fun mlkitCode(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> TranslateLanguage.FRENCH
        AppLanguage.EN -> TranslateLanguage.ENGLISH
        AppLanguage.AR -> TranslateLanguage.ARABIC
    }

    private fun getOrCreateTranslator(target: AppLanguage): Translator {
        return translators.getOrPut(target) {
            Translation.getClient(
                TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.FRENCH)
                    .setTargetLanguage(mlkitCode(target))
                    .build()
            )
        }
    }

    fun toggleLanguage() {
        val next = when (_currentLanguage.value) {
            AppLanguage.FR -> AppLanguage.EN
            AppLanguage.EN -> AppLanguage.AR
            AppLanguage.AR -> AppLanguage.FR
        }
        setLanguage(next)
    }

    fun setLanguage(target: AppLanguage) {
        if (target == AppLanguage.FR) {
            _currentLanguage.value = AppLanguage.FR
            _error.value = null
            return
        }
        prepareLanguage(target)
    }

    private fun prepareLanguage(target: AppLanguage) {
        _isTranslating.value = true
        _error.value = null

        val conditions = DownloadConditions.Builder().build()

        viewModelScope.launch {
            try {
                val translator = getOrCreateTranslator(target)
                translator.downloadModelIfNeeded(conditions).awaitTask()
                _currentLanguage.value = target
            } catch (e: Exception) {
                _error.value = "Traduction indisponible : ${e.localizedMessage ?: "erreur reseau"}"
                _currentLanguage.value = AppLanguage.FR
            } finally {
                _isTranslating.value = false
            }
        }
    }

    suspend fun translate(text: String): String {
        if (_currentLanguage.value == AppLanguage.FR || text.isBlank()) return text
        return try {
            val translator = getOrCreateTranslator(_currentLanguage.value)
            translator.translate(text).awaitTask()
        } catch (e: Exception) {
            _error.value = "Impossible de traduire ce contenu"
            text
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        translators.values.forEach { it.close() }
    }
}