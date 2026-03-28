package br.com.phs.fullexample

import br.com.phs.fullexample.dynamicform.infrastructure.DynamicFormXmlParser
import br.com.phs.fullexample.dynamicform.sample.BundledSampleLoginFormSource
import br.com.phs.fullexample.dynamicform.sample.SampleLoginActionHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DynamicFormEngineTest {
    @Test
    fun parserReadsBundledLoginDefinition() {
        val definition = DynamicFormXmlParser.parse(BundledSampleLoginFormSource.loadXml())

        assertEquals("login-demo", definition.id)
        assertEquals(8, definition.components.size)
    }

    @Test
    fun loginEngineValidatesAndAuthenticates() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        val initialButton = engine.getComponentAt(7)
        assertFalse(initialButton.isEnabled)

        engine.updateTextField("email", "demo@acme.com")
        engine.updateTextField("password", "123456")
        engine.triggerAction("submit")

        assertTrue(engine.getState().isAuthenticated)
        assertTrue(engine.getState().statusMessage.contains("Login efetuado"))
        assertEquals("", engine.getComponentAt(3).value)
    }

    @Test
    fun triggerActionPopulatesValidationErrors() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        engine.triggerAction("submit")

        assertFalse(engine.getState().isAuthenticated)
        assertEquals("Revise os campos destacados.", engine.getState().statusMessage)
        assertTrue(engine.getComponentAt(2).error.contains("obrigatorio"))
        assertTrue(engine.getComponentAt(3).error.contains("obrigatorio"))
    }

    @Test
    fun updateTextFieldClearsStatusAndResetsAuthentication() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        engine.updateTextField("email", "demo@acme.com")
        engine.updateTextField("password", "123456")
        engine.triggerAction("submit")
        engine.updateTextField("email", "changed@acme.com")

        assertFalse(engine.getState().isAuthenticated)
        assertEquals("", engine.getState().statusMessage)
    }

    @Test
    fun updateTextFieldAppliesConfiguredMaxLength() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        engine.updateTextField("password", "1234567890123456789012345678901234567890")

        assertEquals(32, engine.getComponentAt(3).value.length)
    }

    @Test
    fun checkboxStartsCheckedAndCanBeUpdated() {
        val engine = DynamicFormEngine(BundledSampleLoginFormSource, SampleLoginActionHandler)

        assertTrue(engine.getComponentAt(4).checked)
        engine.updateCheckboxField("rememberMe", false)

        assertFalse(engine.getComponentAt(4).checked)
    }
}
