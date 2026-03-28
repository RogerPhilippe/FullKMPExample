package br.com.phs.fullexample.dynamicform.application

import br.com.phs.fullexample.dynamicform.domain.DynamicFormDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentDefinition

internal class DynamicFormSession private constructor(
    val textValues: LinkedHashMap<String, String>,
    val checkboxValues: LinkedHashMap<String, Boolean>,
    val fieldErrors: LinkedHashMap<String, String>,
    var statusMessage: String,
    var isLoading: Boolean,
    var isAuthenticated: Boolean,
) {
    companion object {
        fun fromDefinition(definition: DynamicFormDefinition): DynamicFormSession {
            val textValues = linkedMapOf<String, String>()
            val checkboxValues = linkedMapOf<String, Boolean>()

            definition.components.forEach { component ->
                if (component is FieldComponentDefinition) {
                    if (component.isCheckbox) {
                        checkboxValues[component.id] = component.defaultChecked
                    } else {
                        textValues[component.id] = ""
                    }
                }
            }

            return DynamicFormSession(
                textValues = textValues,
                checkboxValues = checkboxValues,
                fieldErrors = linkedMapOf(),
                statusMessage = "",
                isLoading = false,
                isAuthenticated = false,
            )
        }
    }
}
