package ru.herobrine1st.e621.ui.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.herobrine1st.e621.R
import ru.herobrine1st.e621.ui.component.Base
import ru.herobrine1st.e621.ui.screen.home.HomeViewModel.LoginState

@Composable
fun Home(
    navigateToSearch: () -> Unit,
    navigateToFavorites: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Base {
        Button(
            onClick = navigateToSearch,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.search))
            Icon(Icons.Rounded.NavigateNext, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Crossfade(targetState = viewModel.state) { state ->
            when (state) {
                LoginState.LOADING -> CircularProgressIndicator()
                LoginState.AUTHORIZED -> {
                    Button(
                        onClick = {
                            viewModel.logout()
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.login_logout))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = navigateToFavorites,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.favourites))
                    }
                }
                LoginState.IO_ERROR -> {
                    Text(stringResource(R.string.network_error))
                    Button(onClick = { viewModel.checkAuthorization() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
                else -> AuthorizationMenu { u, p, cb ->
                    viewModel.login(u, p, cb)
                }
            }
        }
    }
}

@Composable
fun AuthorizationMenu(
    onLogin: (username: String, password: String, onSuccess: (LoginState) -> Unit) -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val canLogin by remember { derivedStateOf { username.isNotBlank() && password.isNotBlank() } }

    val login = {
        if (canLogin)
            onLogin(username, password) {
                if (it == LoginState.AUTHORIZED) {
                    username = ""
                    password = ""
                }
            }
    }

    val passwordFieldFocusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text(stringResource(R.string.login_username)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions { passwordFieldFocusRequester.requestFocus() }
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = password,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = { password = it },
        label = { Text(stringResource(R.string.login_password)) },
        singleLine = true,
        modifier = Modifier
            .focusRequester(passwordFieldFocusRequester)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions { login() }
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        onClick = {
            login()
        },
        enabled = canLogin,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.login_login))
    }
    Spacer(modifier = Modifier.height(8.dp))
}