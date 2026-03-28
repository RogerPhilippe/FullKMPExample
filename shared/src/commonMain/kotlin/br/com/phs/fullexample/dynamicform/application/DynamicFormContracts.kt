package br.com.phs.fullexample.dynamicform.application

import br.com.phs.fullexample.dynamicform.domain.DynamicFormDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentDefinition

internal interface DynamicFormSource {
    fun loadXml(): String
}

internal data class DynamicFormActionRequest(
    val actionId: String,
    val textValues: Map<String, String>,
    val checkboxValues: Map<String, Boolean>,
)

internal data class DynamicFormActionResult(
    val isSuccess: Boolean,
    val statusMessage: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val clearTextFields: Set<String> = emptySet(),
)

internal interface DynamicFormActionHandler {
    fun onAction(request: DynamicFormActionRequest): DynamicFormActionResult
}

internal class DynamicFormDefinitionCatalog(
    val definition: DynamicFormDefinition,
) {
    private val componentsById = definition.components.associateBy { it.id }

    val textFields = definition.components.filterIsInstance<FieldComponentDefinition>()
        .filter { it.isTextInput }

    fun componentAt(index: Int) = definition.components[index]

    fun requireTextField(componentId: String): FieldComponentDefinition {
        val field = componentsById[componentId] as? FieldComponentDefinition
        require(field != null && field.isTextInput) { "Campo de texto desconhecido: $componentId" }
        return field
    }

    fun requireCheckboxField(componentId: String): FieldComponentDefinition {
        val field = componentsById[componentId] as? FieldComponentDefinition
        require(field != null && field.isCheckbox) { "Checkbox desconhecido: $componentId" }
        return field
    }
}
