package br.com.phs.fullexample

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private const val SavedUsersKey = "user_registration_users"

internal interface UserStorage {
    fun read(key: String): String?
    fun write(key: String, value: String)
}

internal class UserRepository(
    private val storage: UserStorage,
) {
    fun loadUsers(): List<RegisteredUser> {
        val payload = storage.read(SavedUsersKey).orEmpty()
        if (payload.isBlank()) return emptyList()

        return payload
            .lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull(::decodeUserOrNull)
            .toList()
    }

    fun saveUsers(users: List<RegisteredUser>) {
        val payload = users.joinToString(separator = "\n", transform = ::encodeUser)
        storage.write(SavedUsersKey, payload)
    }

    private fun encodeUser(user: RegisteredUser): String {
        return listOf(user.name, user.email, user.phone)
            .joinToString(separator = "|", transform = ::escape)
    }

    private fun decodeUserOrNull(line: String): RegisteredUser? {
        val parts = mutableListOf<String>()
        val token = StringBuilder()
        var escaped = false

        line.forEach { char ->
            if (escaped) {
                token.append(
                    when (char) {
                        'n' -> '\n'
                        'p' -> '|'
                        '\\' -> '\\'
                        else -> char
                    }
                )
                escaped = false
                return@forEach
            }

            when (char) {
                '\\' -> escaped = true
                '|' -> {
                    parts += token.toString()
                    token.clear()
                }

                else -> token.append(char)
            }
        }

        if (escaped) token.append('\\')
        parts += token.toString()

        if (parts.size != 3) return null

        return RegisteredUser(
            name = parts[0],
            email = parts[1],
            phone = parts[2],
        )
    }

    private fun escape(value: String): String {
        return buildString {
            value.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '|' -> append("\\p")
                    '\n' -> append("\\n")
                    else -> append(char)
                }
            }
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
enum class UserRegistrationScreen {
    REGISTER,
    USERS,
}

@OptIn(ExperimentalJsExport::class)
@JsExport
data class RegisteredUser(
    val name: String,
    val email: String,
    val phone: String,
)

@OptIn(ExperimentalJsExport::class)
@JsExport
data class UserRegistrationState(
    val screen: UserRegistrationScreen,
    val isRegisterScreen: Boolean,
    val isUsersScreen: Boolean,
    val title: String,
    val name: String,
    val email: String,
    val phone: String,
    val users: Array<RegisteredUser>,
    val message: String,
)

open class UserRegistrationEngine internal constructor(
    storage: UserStorage,
) {
    private val repository = UserRepository(storage)

    private var currentScreen = UserRegistrationScreen.USERS
    private var name = ""
    private var email = ""
    private var phone = ""
    private var message = ""
    private var users = repository.loadUsers()

    fun getState(): UserRegistrationState {
        return UserRegistrationState(
            screen = currentScreen,
            isRegisterScreen = currentScreen == UserRegistrationScreen.REGISTER,
            isUsersScreen = currentScreen == UserRegistrationScreen.USERS,
            title = "Cadastro de Usuarios",
            name = name,
            email = email,
            phone = phone,
            users = users.toTypedArray(),
            message = message,
        )
    }

    fun updateName(value: String) {
        name = value
        clearMessage()
    }

    fun updateEmail(value: String) {
        email = value
        clearMessage()
    }

    fun updatePhone(value: String) {
        phone = value
        clearMessage()
    }

    fun saveUser() {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()

        message = when {
            trimmedName.isEmpty() -> "Nome e obrigatorio."
            trimmedEmail.isEmpty() -> "Email e obrigatorio."
            !trimmedEmail.contains("@") || !trimmedEmail.contains(".") -> "Email invalido."
            trimmedPhone.isEmpty() -> "Telefone e obrigatorio."
            else -> {
                users = users + RegisteredUser(
                    name = trimmedName,
                    email = trimmedEmail,
                    phone = trimmedPhone,
                )
                repository.saveUsers(users)
                name = ""
                email = ""
                phone = ""
                currentScreen = UserRegistrationScreen.USERS
                "Usuario salvo."
            }
        }
    }

    fun openUsers() {
        currentScreen = UserRegistrationScreen.USERS
        clearMessage()
    }

    fun openRegister() {
        currentScreen = UserRegistrationScreen.REGISTER
        clearMessage()
    }

    fun getUsersCount(): Int = users.size

    fun getUserAt(index: Int): RegisteredUser = users[index]

    private fun clearMessage() {
        message = ""
    }
}
