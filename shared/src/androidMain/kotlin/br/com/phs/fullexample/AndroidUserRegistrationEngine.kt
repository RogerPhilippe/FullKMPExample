package br.com.phs.fullexample

import android.content.Context
import androidx.core.content.edit

private class AndroidUserStorage(
    context: Context,
) : UserStorage {
    private val sharedPreferences =
        context.applicationContext.getSharedPreferences("user_registration_storage", Context.MODE_PRIVATE)

    override fun read(key: String): String? = sharedPreferences.getString(key, null)

    override fun write(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }
}

class AndroidUserRegistrationEngine(
    context: Context,
) : UserRegistrationEngine(AndroidUserStorage(context))
