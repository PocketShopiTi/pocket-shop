package com.iti.pocketshop.core.tutorial

import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TutorialManager {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _targetBounds = MutableStateFlow<Map<Int, Rect>>(emptyMap())
    val targetBounds: StateFlow<Map<Int, Rect>> = _targetBounds.asStateFlow()

    fun reportTarget(step: Int, bounds: Rect) {
        _targetBounds.update { it + (step to bounds) }
    }

    fun nextStep() {
        _currentStep.update { it + 1 }
    }

    fun endTutorial() {
        _currentStep.value = -1 // Indicates finished
    }

    fun startStage(startStep: Int) {
        _currentStep.value = startStep
    }

    fun reset() {
        _currentStep.value = 0
        _targetBounds.value = emptyMap()
    }
}
