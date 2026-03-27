package br.com.phs.fullexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    val context = LocalContext.current
    val engine = remember { AndroidUserRegistrationEngine(context) }
    var state by remember { mutableStateOf(engine.getState()) }

    fun refreshState() {
        state = engine.getState()
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(24.dp),
            ) {
                when (state.screen) {
                    UserRegistrationScreen.REGISTER -> RegisterScreen(
                        state = state,
                        onNameChange = {
                            engine.updateName(it)
                            refreshState()
                        },
                        onEmailChange = {
                            engine.updateEmail(it)
                            refreshState()
                        },
                        onPhoneChange = {
                            engine.updatePhone(it)
                            refreshState()
                        },
                        onSaveClick = {
                            engine.saveUser()
                            refreshState()
                        },
                        onUsersClick = {
                            engine.openUsers()
                            refreshState()
                        },
                    )

                    UserRegistrationScreen.USERS -> UsersScreen(
                        state = state,
                        onBackClick = {
                            engine.openRegister()
                            refreshState()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterScreen(
    state: UserRegistrationState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onUsersClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineMedium,
        )

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        if (state.message.isNotBlank()) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSaveClick) {
                Text("Salvar")
            }

            Button(onClick = onUsersClick) {
                Text("Usuarios")
            }
        }
    }
}

@Composable
private fun UsersScreen(
    state: UserRegistrationState,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Usuarios Cadastrados",
            style = MaterialTheme.typography.headlineMedium,
        )

        if (state.users.isEmpty()) {
            Text("Nenhum usuario cadastrado.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.users.toList()) { user ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(user.email)
                            Text(user.phone)
                        }
                    }
                }
            }
        }

        Button(onClick = onBackClick) {
            Text("Cadastrar")
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            state = UserRegistrationState(
                screen = UserRegistrationScreen.REGISTER,
                isRegisterScreen = true,
                isUsersScreen = false,
                title = "Cadastro de Usuarios",
                name = "Ana",
                email = "ana@email.com",
                phone = "11999990000",
                users = emptyArray(),
                message = "",
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onSaveClick = {},
            onUsersClick = {},
        )
    }
}
