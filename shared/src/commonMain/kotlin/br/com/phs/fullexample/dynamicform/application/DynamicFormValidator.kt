package br.com.phs.fullexample.dynamicform.application

import br.com.phs.fullexample.dynamicform.domain.FieldComponentDefinition

internal class DynamicFormValidator {
    fun sanitize(field: FieldComponentDefinition, value: String): String {
        val maxLength = field.maxLength
        if (maxLength == null || value.length <= maxLength) return value
        return value.take(maxLength)
    }

    fun validateAll(
        catalog: DynamicFormDefinitionCatalog,
        session: DynamicFormSession,
    ): Map<String, String> {
        val errors = linkedMapOf<String, String>()

        catalog.textFields.forEach { field ->
            validateField(field, session.textValues[field.id].orEmpty())?.let { error ->
                errors[field.id] = error
            }
        }

        return errors
    }

    fun canTriggerActions(
        catalog: DynamicFormDefinitionCatalog,
        session: DynamicFormSession,
    ): Boolean {
        if (session.isLoading) return false

        return catalog.textFields.none { field ->
            field.required && session.textValues[field.id].orEmpty().isBlank()
        }
    }

    private fun validateField(field: FieldComponentDefinition, rawValue: String): String? {
        val trimmedValue = rawValue.trim()

        if (field.required && trimmedValue.isBlank()) {
            return "${field.label} e obrigatorio."
        }
        if (trimmedValue.isBlank()) {
            return null
        }
        if (field.minLength != null && trimmedValue.length < field.minLength) {
            return "${field.label} deve ter ao menos ${field.minLength} caracteres."
        }
        if (field.maxLength != null && trimmedValue.length > field.maxLength) {
            return "${field.label} deve ter no maximo ${field.maxLength} caracteres."
        }
        if (field.pattern != null && !Regex(field.pattern).matches(trimmedValue)) {
            return "${field.label} esta em formato invalido."
        }
        return null
    }
}
