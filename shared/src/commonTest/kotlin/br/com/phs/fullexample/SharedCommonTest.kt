package br.com.phs.fullexample

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedCommonTest {
    private class InMemoryUserStorage : UserStorage {
        private val values = mutableMapOf<String, String>()

        override fun read(key: String): String? = values[key]

        override fun write(key: String, value: String) {
            values[key] = value
        }
    }

    @Test
    fun savesUsersAndKeepsThemAvailableForListing() {
        val engine = UserRegistrationEngine(InMemoryUserStorage())

        engine.updateName("Ana")
        engine.updateEmail("ana@email.com")
        engine.updatePhone("11999990000")
        engine.saveUser()

        val state = engine.getState()

        assertEquals("Usuario salvo.", state.message)
        assertEquals(1, state.users.size)
        assertEquals("Ana", state.users.first().name)
        assertEquals("", state.name)
    }

    @Test
    fun rejectsInvalidEmail() {
        val engine = UserRegistrationEngine(InMemoryUserStorage())

        engine.updateName("Ana")
        engine.updateEmail("ana-email.com")
        engine.updatePhone("11999990000")
        engine.saveUser()

        val state = engine.getState()

        assertEquals("Email invalido.", state.message)
        assertEquals(0, state.users.size)
    }
}
