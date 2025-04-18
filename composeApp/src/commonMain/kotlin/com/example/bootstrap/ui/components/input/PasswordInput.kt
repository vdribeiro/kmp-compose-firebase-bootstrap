package com.example.bootstrap.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun PasswordInput(passwordInputContent: PasswordInputContent) = with(passwordInputContent) {
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        value = password,
        enabled = enabled,
        onValueChange = {
            password = it
            onValueChange(it)
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Lock else Icons.Outlined.Lock,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        }
    )
}

internal data class PasswordInputContent(
    val enabled: Boolean,
    val onValueChange: (String) -> Unit
)
