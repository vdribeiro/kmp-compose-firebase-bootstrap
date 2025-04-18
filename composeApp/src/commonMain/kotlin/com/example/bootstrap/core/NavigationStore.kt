package com.example.bootstrap.core

import androidx.compose.runtime.Composable
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.config.remoteConfig
import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.toggle.FeatureToggle
import com.example.bootstrap.ui.Store
import com.example.bootstrap.usecase.UseCases
import com.example.bootstrap.usecase.session.ui.SessionScreen
import com.example.bootstrap.usecase.session.ui.SessionStore
import kotlinx.coroutines.flow.Flow

internal sealed interface Action {
    data class GoTo(val screen: Screen): Action
    data object Refresh: Action
}

internal data class State(
    val enabledScreenMap: Map<Screen, Boolean> = emptyMap(),
    val screen: Screen? = null,
    val currentStore: Store<*, *>? = null,
    val content: @Composable () -> Unit = { }
)

internal enum class Screen {
    SESSION,
}

internal class NavigationStore(
    private val toggle: FeatureToggle,
    private val dispatcher: Dispatcher,
    private val userId: Flow<String?>,
    private val useCases: UseCases
): Store<Action, State>(
    dispatcher = dispatcher,
    userId = userId,
    initialState = State()
) {
    init {
        launch {
            sync()
        }
        send(action = Action.GoTo(screen = Screen.SESSION))
    }

    override fun reducer(state: State, action: Action) {
        when (action) {
            is Action.GoTo -> {
                state.currentStore?.close()
                val currentStore: Store<*, *>
                val content: @Composable () -> Unit
                when (action.screen) {
                    Screen.SESSION -> {
                        currentStore = SessionStore(
                            dispatcher = dispatcher,
                            userId = userId,
                            sessionUseCases = useCases.session
                        )
                        content = { SessionScreen(store = currentStore) }
                    }
                }
                updateState {
                    it.copy(
                        screen = action.screen,
                        currentStore = currentStore,
                        content = content
                    )
                }
            }

            Action.Refresh -> launch { sync() }
        }
    }

    // TODO - Temporary while push payload is not implemented
    private suspend fun sync() {
        Firebase.remoteConfig.fetchAndActivate()

        useCases.session.syncUsers()
    }

    override fun newUser(userId: String?) {
        if (userId.isNullOrBlank()) send(action = Action.GoTo(screen = Screen.SESSION))
    }
}
