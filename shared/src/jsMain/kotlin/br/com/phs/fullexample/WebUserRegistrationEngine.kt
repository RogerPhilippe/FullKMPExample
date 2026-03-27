package br.com.phs.fullexample

import kotlinx.browser.window
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private class WebUserStorage : UserStorage {
    override fun read(key: String): String? = window.localStorage.getItem(key)

    override fun write(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class WebUserRegistrationEngine {
    private val delegate = UserRegistrationEngine(WebUserStorage())

    fun getState(): UserRegistrationState = delegate.getState()

    fun updateName(value: String) {
        delegate.updateName(value)
    }

    fun updateEmail(value: String) {
        delegate.updateEmail(value)
    }

    fun updatePhone(value: String) {
        delegate.updatePhone(value)
    }

    fun saveUser() {
        delegate.saveUser()
    }

    fun openUsers() {
        delegate.openUsers()
    }

    fun openRegister() {
        delegate.openRegister()
    }
}
