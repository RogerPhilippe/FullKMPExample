package br.com.phs.fullexample

import br.com.phs.fullexample.dynamicform.application.DynamicFormActionExecutor
import br.com.phs.fullexample.dynamicform.application.DynamicFormActionHandler
import br.com.phs.fullexample.dynamicform.application.DynamicFormDefinitionCatalog
import br.com.phs.fullexample.dynamicform.application.DynamicFormSession
import br.com.phs.fullexample.dynamicform.application.DynamicFormSource
import br.com.phs.fullexample.dynamicform.application.DynamicFormValidator
import br.com.phs.fullexample.dynamicform.infrastructure.DynamicFormXmlParser
import br.com.phs.fullexample.dynamicform.presentation.DynamicComponentStateMapper

open class DynamicFormEngine internal constructor(
    source: DynamicFormSource,
    actionHandler: DynamicFormActionHandler,
) {
    private val catalog = DynamicFormDefinitionCatalog(DynamicFormXmlParser.parse(source.loadXml()))
    private val session = DynamicFormSession.fromDefinition(catalog.definition)
    private val validator = DynamicFormValidator()
    private val actionExecutor = DynamicFormActionExecutor(actionHandler, validator)

    fun getState(): DynamicFormState {
        return DynamicFormState(
            formId = catalog.definition.id,
            statusMessage = session.statusMessage,
            isLoading = session.isLoading,
            isAuthenticated = session.isAuthenticated,
        )
    }

    fun getComponentsCount(): Int = catalog.definition.components.size

    fun getComponentAt(index: Int): DynamicComponentState {
        return DynamicComponentStateMapper.map(
            component = catalog.componentAt(index),
            session = session,
            actionsEnabled = validator.canTriggerActions(catalog, session),
        )
    }

    fun updateTextField(componentId: String, value: String) {
        val field = catalog.requireTextField(componentId)
        session.textValues[componentId] = validator.sanitize(field, value)
        session.fieldErrors.remove(componentId)
        session.statusMessage = ""
        session.isAuthenticated = false
    }

    fun updateCheckboxField(componentId: String, checked: Boolean) {
        catalog.requireCheckboxField(componentId)
        session.checkboxValues[componentId] = checked
        session.fieldErrors.remove(componentId)
        session.statusMessage = ""
    }

    fun triggerAction(actionId: String) {
        actionExecutor.trigger(actionId, catalog, session)
    }
}
