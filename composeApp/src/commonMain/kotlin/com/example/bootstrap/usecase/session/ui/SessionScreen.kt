package com.example.bootstrap.usecase.session.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bootstrap.ui.Store
import com.example.bootstrap.ui.components.session.Credentials
import com.example.bootstrap.ui.components.session.CredentialsContent
import com.example.bootstrap.ui.components.session.UserDetails
import com.example.bootstrap.ui.components.session.UserDetailsContent

@Composable
internal fun SessionScreen(store: Store<Action, State>) {
    val storeState by store.stateFlow.collectAsState()

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.padding(all = 32.dp))

            when (val user = storeState.state.user) {
                null -> Credentials(
                    credentialsContent = CredentialsContent(
                        enabled = !storeState.working,
                        onChangeEmail = { store.send(action = Action.ChangeEmail(email = it)) },
                        onChangePassword = { store.send(action = Action.ChangePassword(password = it)) },
                        onClick = { store.send(action = Action.SignIn) },
                        errorVisible = storeState.state.errorVisible
                    )
                )

                else -> UserDetails(
                    userDetailsContent = UserDetailsContent(
                        enabled = !storeState.working,
                        user = user,
                        onRegisterUser = { email, password ->
                            store.send(action = Action.RegisterNewUser(email = email, password = password))
                        },
                        onUpdatePassword = { store.send(action = Action.UpdatePassword(password = it)) },
                        onUpdateName = { store.send(action = Action.UpdateName(name = it)) },
                        onUpdatePhotoUrl = { store.send(action = Action.UpdatePhotoUrl(photoUrl = it)) },
                        onSignOut = { store.send(action = Action.SignOut) }
                    )
                )
            }
        }
    }
}
