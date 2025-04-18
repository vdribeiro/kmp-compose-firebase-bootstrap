package com.example.bootstrap.ui

import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.flow.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal abstract class Store<Action, State>(
    private val dispatcher: Dispatcher,
    private val userId: Flow<String?>,
    initialState: State
) {
    private val _stateFlow: MutableStateFlow<StoreState<State>> = MutableStateFlow(value = StoreState(state = initialState))
    val stateFlow: StateFlow<StoreState<State>> get() = _stateFlow
    private val jobs = mutableListOf<Job>()

    init {
        observe {
            userId.collect { userId ->
                dispatcher.main.launch { _stateFlow.update { it.copy(userId = userId) } }
                newUser(userId = userId)
            }
        }
    }

    fun send(action: Action) = reducer(state = _stateFlow.value.state, action = action)

    protected abstract fun reducer(state: State, action: Action)

    protected fun updateState(body: (State) -> State) =
        dispatcher.main.launch { _stateFlow.update { it.copy(state = body(_stateFlow.value.state)) } }

    protected open fun newUser(userId: String?) {}

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        dispatcher.main.launch { _stateFlow.update { it.copy(working = true) } }
        dispatcher.io.launch {
            block()
            dispatcher.main.launch { _stateFlow.update { it.copy(working = false) } }
        }
    }

    protected fun observe(block: suspend CoroutineScope.() -> Unit): Job =
        dispatcher.io.launch(block = block).also { jobs.add(element = it) }

    fun close() = jobs.forEach { it.cancel() }
}
