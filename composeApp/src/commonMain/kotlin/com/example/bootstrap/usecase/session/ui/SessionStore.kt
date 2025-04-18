package com.example.bootstrap.usecase.session.ui

import com.example.bootstrap.flow.Dispatcher
import com.example.bootstrap.ui.Store
import com.example.bootstrap.usecase.session.SessionUseCases
import com.example.bootstrap.usecase.session.model.params.Credentials
import com.example.bootstrap.usecase.session.model.result.User
import com.example.bootstrap.usecase.session.model.result.UserResult
import kotlinx.coroutines.flow.Flow

internal sealed interface Action {
    data class ChangeEmail(val email: String): Action
    data class ChangePassword(val password: String): Action
    data object SignIn: Action
    data class RegisterNewUser(val email: String, val password: String): Action
    data class UpdatePassword(val password: String): Action
    data class UpdateName(val name: String?): Action
    data class UpdatePhotoUrl(val photoUrl: String?): Action
    data object SignOut: Action
}

internal data class State(
    val user: User? = null,
    val email: String = "",
    val password: String = "",
    val errorVisible: Boolean = false
)

internal class SessionStore(
    dispatcher: Dispatcher,
    userId: Flow<String?>,
    private val sessionUseCases: SessionUseCases,
): Store<Action, State>(
    dispatcher = dispatcher,
    userId = userId,
    initialState = State()
) {
    override fun reducer(state: State, action: Action) {
        when (action) {
            is Action.ChangeEmail -> updateState { it.copy(email = action.email, errorVisible = false) }
            is Action.ChangePassword -> updateState { it.copy(password = action.password, errorVisible = false) }
            is Action.SignIn -> launch {
                val errorVisible = when (sessionUseCases.signInWithEmailAndPassword(
                    credentials = Credentials(
                        email = state.email,
                        password = state.password
                    )
                )) {
                    is UserResult.Success -> false
                    is UserResult.Error -> true
                }
                updateState { it.copy(errorVisible = errorVisible) }
            }

            is Action.RegisterNewUser -> launch {
                val errorVisible = when (sessionUseCases.createUserWithEmailAndPassword(
                    credentials = Credentials(
                        email = action.email,
                        password = action.password
                    )
                )) {
                    is UserResult.Success -> false
                    is UserResult.Error -> true
                }
                updateState { it.copy(errorVisible = errorVisible) }
            }

            is Action.UpdatePassword -> launch {
                sessionUseCases.updateUserPassword(password = action.password)
            }

            is Action.UpdateName -> launch {
                sessionUseCases.updateUserName(name = action.name)
            }

            is Action.UpdatePhotoUrl -> launch {
                sessionUseCases.updateUserPhotoUrl(photoUrl = action.photoUrl)
            }

            Action.SignOut -> launch {
                sessionUseCases.signOut()
                updateState {
                    it.copy(
                        email = "",
                        password = "",
                        errorVisible = false
                    )
                }
            }
        }
    }

    override fun newUser(userId: String?) = launch {
        val user = userId?.let { sessionUseCases.getUserById(id = it) }
        updateState { it.copy(user = user) }
    }
}
