package br.com.phs.fullexample.dynamicform.application

internal class DynamicFormActionExecutor(
    private val actionHandler: DynamicFormActionHandler,
    private val validator: DynamicFormValidator,
) {
    fun trigger(
        actionId: String,
        catalog: DynamicFormDefinitionCatalog,
        session: DynamicFormSession,
    ) {
        if (session.isLoading) return

        val fieldErrors = validator.validateAll(catalog, session)
        session.fieldErrors.clear()
        session.fieldErrors.putAll(fieldErrors)

        if (fieldErrors.isNotEmpty()) {
            session.statusMessage = "Revise os campos destacados."
            session.isAuthenticated = false
            return
        }

        session.isLoading = true
        val result = actionHandler.onAction(
            DynamicFormActionRequest(
                actionId = actionId,
                textValues = session.textValues.toMap(),
                checkboxValues = session.checkboxValues.toMap(),
            ),
        )
        session.isLoading = false
        session.statusMessage = result.statusMessage
        session.isAuthenticated = result.isSuccess
        session.fieldErrors.clear()
        session.fieldErrors.putAll(result.fieldErrors)

        result.clearTextFields.forEach { fieldId ->
            if (session.textValues.containsKey(fieldId)) {
                session.textValues[fieldId] = ""
            }
        }
    }
}
