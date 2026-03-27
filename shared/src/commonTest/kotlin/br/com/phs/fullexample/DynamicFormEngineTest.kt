package br.com.phs.fullexample

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DynamicFormEngineTest {
    @Test
    fun parserReadsBundledLoginDefinition() {
        val definition = DynamicFormXmlParser.parse(BundledSampleLoginFormSource.loadXml())

        assertEquals("login-demo", definition.id)
        assertEquals(7, definition.components.size)
    }

    @Test
    fun loginEngineValidatesAndAuthenticates() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        val initialButton = engine.getComponentAt(6)
        assertFalse(initialButton.isEnabled)

        engine.updateTextField("email", "demo@acme.com")
        engine.updateTextField("password", "123456")
        engine.triggerAction("submit")

        assertTrue(engine.getState().isAuthenticated)
        assertTrue(engine.getState().statusMessage.contains("Login efetuado"))
        assertEquals("", engine.getComponentAt(5).value)
    }
}
