package com.example.utilities

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Holds a tool's input fields as typed; each tool works out its result from them. */
abstract class ToolViewModel<S>(private val initial: S) : ViewModel() {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    fun update(change: (S) -> S) = _state.update(change)

    fun reset() {
        _state.value = initial
    }
}
